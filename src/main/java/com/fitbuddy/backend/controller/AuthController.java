package com.fitbuddy.backend.controller;

import com.fitbuddy.backend.dto.AuthResponse;
import com.fitbuddy.backend.dto.CurrentUserResponse;
import com.fitbuddy.backend.dto.LoginRequest;
import com.fitbuddy.backend.dto.RegisterRequest;
import com.fitbuddy.backend.entity.User;
import com.fitbuddy.backend.repository.UserRepository;
import com.fitbuddy.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        validateRegisterRequest(request);
        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() == null ? User.Role.STUDENT : request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        UserDetails principal = toPrincipal(savedUser);
        return buildAuthResponse(savedUser, jwtService.generateToken(principal));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        validateLoginRequest(request);
        String email = normalizeEmail(request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        return buildAuthResponse(user, jwtService.generateToken((UserDetails) authentication.getPrincipal()));
    }

    @GetMapping("/me")
    public CurrentUserResponse me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found"));

        return toCurrentUserResponse(user);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .user(toCurrentUserResponse(user))
                .build();
    }

    private CurrentUserResponse toCurrentUserResponse(User user) {
        return CurrentUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private UserDetails toPrincipal(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null
                || isBlank(request.getName())
                || isBlank(request.getEmail())
                || isBlank(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, email, and password are required");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null || isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
