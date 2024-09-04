package com.intern.onboarding.api.auth.dto;

import com.intern.onboarding.domain.user.Role;
import com.intern.onboarding.domain.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public record SignUpRequest(String username, String password, String nickname) {
    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(this.username)
                .password(passwordEncoder.encode(this.password))
                .nickname(this.nickname)
                .role(Role.USER)
                .build();
    }
}
