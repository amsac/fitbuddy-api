package com.fitbuddy.backend;

import com.fitbuddy.backend.repository.UserRepository;
import com.fitbuddy.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;
    @Autowired ObjectMapper json;

    @Test
    void registrationHashesPasswordAndReturnsSignedToken() throws Exception {
        String response = mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Alice","email":"  ALICE@example.com  ","password":"password123","role":"STUDENT"}
                    """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400))
                .andExpect(jsonPath("$.user.email").value("alice@example.com"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        var user = users.findByEmail("alice@example.com").orElseThrow();
        assertThat(encoder.matches("password123", user.getPassword())).isTrue();
        assertThat(user.isActive()).isTrue();
        assertThat(user.getCreatedAt()).isNotNull();
        var claims = jwt.parseClaims(json.readTree(response).get("accessToken").asText());
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
        assertThat(claims.get("userId", Long.class)).isEqualTo(user.getId());
        assertThat(claims.get("role")).isEqualTo("STUDENT");
        assertThat(claims).doesNotContainKeys("password");
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Alice","email":"ALICE@example.com","password":"password123","role":"TRAINER"}
                    """))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void invalidRegistrationIsRejected() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":" ","email":"invalid","password":"short"}
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists())
                .andExpect(jsonPath("$.fieldErrors.role").exists());
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Alice","email":"valid@example.com","password":"password123","role":"ADMIN"}
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginMeAndInvalidTokens() throws Exception {
        String token = register("login@example.com", "TRAINER");
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("TRAINER"))
                .andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":" LOGIN@example.com ","password":"password123"}
                    """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isString());
        for (String email : new String[]{"login@example.com", "missing@example.com"}) {
            mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"" + email + "\",\"password\":\"incorrect\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }
        var key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="));
        String expired = io.jsonwebtoken.Jwts.builder().subject("login@example.com")
                .expiration(java.util.Date.from(java.time.Instant.now().minusSeconds(10)))
                .signWith(key).compact();
        String wrongSignature = io.jsonwebtoken.Jwts.builder().subject("login@example.com")
                .expiration(java.util.Date.from(java.time.Instant.now().plusSeconds(60)))
                .signWith(io.jsonwebtoken.Jwts.SIG.HS256.key().build()).compact();
        for (String invalid : new String[]{"malformed", expired, wrongSignature}) {
            mvc.perform(get("/auth/me").header("Authorization", "Bearer " + invalid))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Authentication required"));
        }
        for (String path : new String[]{"/auth/me", "/exercises", "/templates", "/sessions/me/completed"}) {
            mvc.perform(get(path)).andExpect(status().isUnauthorized());
        }
        mvc.perform(get("/exercises").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(header().doesNotExist("Set-Cookie"));
        var user = users.findByEmail("login@example.com").orElseThrow();
        user.setActive(false);
        users.saveAndFlush(user);
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"login@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Autowired com.fitbuddy.backend.repository.ExerciseRepository exercises;
    @Autowired com.fitbuddy.backend.repository.WorkoutTemplateRepository templates;
    @Autowired com.fitbuddy.backend.repository.WorkoutSessionRepository sessions;
    @Autowired com.fitbuddy.backend.repository.SetLogRepository sets;

    @Test
    void workoutIdentityAndOwnershipForBothRoles() throws Exception {
        String owner = register("owner@example.com", "STUDENT");
        String other = register("other@example.com", "TRAINER");
        var exercise = exercises.saveAndFlush(com.fitbuddy.backend.entity.Exercise.builder()
                .name("Squat").externalId("squat-auth-test").build());
        long templateId = 0;
        for (String token : new String[]{owner, other}) {
            var response = mvc.perform(post("/templates").header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Workout","description":"Test","level":"BEGINNER","createdBy":999999,
                         "exercises":[{"exerciseId":%d,"targetSets":3,"targetReps":10}]}
                        """.formatted(exercise.getId())))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            templateId = json.readTree(response).get("id").asLong();
            assertThat(templates.findById(templateId).orElseThrow().getCreatedBy().getId())
                    .isEqualTo(jwt.parseClaims(token).get("userId", Long.class));
        }
        String started = mvc.perform(post("/sessions/start").header("Authorization", "Bearer " + owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"templateId\":" + templateId + ",\"userId\":999999}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var session = json.readTree(started);
        long sessionId = session.get("sessionId").asLong();
        long logId = session.get("exercises").get(0).get("exerciseLogId").asLong();
        assertThat(sessions.findById(sessionId).orElseThrow().getUser().getId())
                .isEqualTo(jwt.parseClaims(owner).get("userId", Long.class));
        mvc.perform(get("/sessions/" + sessionId).header("Authorization", "Bearer " + other))
                .andExpect(status().isNotFound());
        mvc.perform(post("/sessions/" + sessionId + "/complete").header("Authorization", "Bearer " + other))
                .andExpect(status().isNotFound());
        assertThat(sessions.findById(sessionId).orElseThrow().isCompleted()).isFalse();
        mvc.perform(post("/sets").header("Authorization", "Bearer " + other)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseLogId\":" + logId + ",\"reps\":10,\"weight\":40}"))
                .andExpect(status().isNotFound());
        assertThat(sets.findByExerciseLogId(logId)).isEmpty();
        for (int i = 1; i <= 2; i++) {
            mvc.perform(post("/sets").header("Authorization", "Bearer " + owner)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"exerciseLogId\":" + logId + ",\"reps\":10,\"weight\":40}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.setNumber").value(i));
        }
        mvc.perform(get("/sessions/" + sessionId).header("Authorization", "Bearer " + owner))
                .andExpect(status().isOk()).andExpect(jsonPath("$.exercises[0].sets.length()").value(2));
        mvc.perform(post("/sessions/" + sessionId + "/complete").header("Authorization", "Bearer " + owner))
                .andExpect(status().isOk());
        mvc.perform(get("/sessions/me/completed").header("Authorization", "Bearer " + owner))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].sessionId").value(sessionId))
                .andExpect(jsonPath("$[0].exercises[0].sets.length()").value(2));
        mvc.perform(get("/sessions/me/completed").header("Authorization", "Bearer " + other))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/sessions/users/1/completed").header("Authorization", "Bearer " + owner))
                .andExpect(status().isNotFound());
    }

    @Test
    void browserPreflightAllowsAuthorizationHeader() throws Exception {
        mvc.perform(options("/auth/register").header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk()).andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void healthIsPublicWithoutExposingOtherEndpoints() throws Exception {
        mvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"UP\"}"))
                .andExpect(header().string("Cache-Control", "no-store"));
        mvc.perform(head("/health")).andExpect(status().isOk());
        mvc.perform(post("/health")).andExpect(status().isUnauthorized());
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/templates")).andExpect(status().isUnauthorized());
    }

    private String register(String email, String role) throws Exception {
        String response = mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Test User","email":"%s","password":"password123","role":"%s"}
                    """.formatted(email, role)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("accessToken").asText();
    }
}
