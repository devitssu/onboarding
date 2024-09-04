package com.intern.onboarding.infra.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal {
    private final Long id;
    private final Collection<GrantedAuthority> authorities;

    public UserPrincipal(Long id, Set<String> roles) {
        this.id = id;
        this.authorities = roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toSet());
    }
}
