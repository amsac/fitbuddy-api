package com.fitbuddy.backend.dto.auth;

import com.fitbuddy.backend.entity.User;

public record CurrentUserResponse(Long id, String name, String email, User.Role role) {
    public static CurrentUserResponse from(User user) {
        return new CurrentUserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
