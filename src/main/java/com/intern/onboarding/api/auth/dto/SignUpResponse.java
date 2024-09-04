package com.intern.onboarding.api.auth.dto;

import com.intern.onboarding.domain.user.Role;
import com.intern.onboarding.domain.user.User;

import java.util.Set;

public record SignUpResponse(String username, String nickname, Set<Role> authorities) {

    public static SignUpResponse from(User user) {
        return new SignUpResponse(
                user.getUsername(),
                user.getNickname(),
                Set.of(user.getRole())
        );
    }
}
