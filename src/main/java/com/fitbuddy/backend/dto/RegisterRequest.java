package com.fitbuddy.backend.dto;

import com.fitbuddy.backend.entity.User;
import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private User.Role role;
}
