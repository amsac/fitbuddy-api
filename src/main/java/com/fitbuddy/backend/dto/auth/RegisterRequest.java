package com.fitbuddy.backend.dto.auth;

import com.fitbuddy.backend.entity.User.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Locale;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank @Size(max = 100)
    private String name;
    @NotBlank @Email @Size(max = 254)
    private String email;
    @NotBlank @Size(min = 8, max = 72)
    private String password;
    @NotNull
    private Role role;

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
