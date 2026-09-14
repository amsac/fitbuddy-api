package com.fitbuddy.backend.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import com.fitbuddy.backend.dto.auth.*;
import com.fitbuddy.backend.entity.User;
import com.fitbuddy.backend.exception.DuplicateEmailException;
import com.fitbuddy.backend.repository.UserRepository;
import com.fitbuddy.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CurrentUserService currentUserService;

    public AuthResponse login(LoginRequest request) {
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadCredentialsException("Invalid email or password");
        }
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new BadCredentialsException("Invalid email or password"));
        return response(user);
    }

    public CurrentUserResponse getCurrentUser() {
        return CurrentUserResponse.from(currentUserService.getCurrentUser());
    }


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must not exceed 72 UTF-8 bytes");
        }
        if (userRepository.existsByEmail(request.getEmail())) throw new DuplicateEmailException();
        User user = User.builder().name(request.getName().trim()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).build();
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            // The unique email constraint also protects simultaneous registrations.
            throw new DuplicateEmailException();
        }
        return response(user);
    }

    private AuthResponse response(User user) {
        return new AuthResponse(jwtService.generateToken(user), "Bearer", jwtService.getExpiresIn(),
                CurrentUserResponse.from(user));
    }
}
