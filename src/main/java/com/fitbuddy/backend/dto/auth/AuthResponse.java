package com.fitbuddy.backend.dto.auth;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, CurrentUserResponse user) {}
