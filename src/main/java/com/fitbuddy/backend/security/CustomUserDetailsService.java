package com.fitbuddy.backend.security;

import com.fitbuddy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        if (user.getRole() == null) throw new UsernameNotFoundException("Invalid email or password");
        // Existing accounts without a password cannot log in until provisioned with a BCrypt hash.
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .disabled(!user.isActive() || user.getPassword() == null || user.getRole() == null)
                .build();
    }
}
