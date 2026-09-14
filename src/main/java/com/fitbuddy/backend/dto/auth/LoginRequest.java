package com.fitbuddy.backend.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Locale;

@Getter
@Setter
public class LoginRequest {
    @NotBlank @Email @Size(max = 254)
    private String email;
    @NotBlank @Size(max = 72)
    private String password;

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
