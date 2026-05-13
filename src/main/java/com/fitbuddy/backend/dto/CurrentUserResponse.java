package com.fitbuddy.backend.dto;

import com.fitbuddy.backend.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentUserResponse {

    private Long id;
    private String name;
    private String email;
    private User.Role role;
}
