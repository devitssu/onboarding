package com.intern.onboarding.infra.security.jwt;

import com.intern.onboarding.infra.security.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UserPrincipal principal;

    JwtAuthenticationToken(UserPrincipal principal, WebAuthenticationDetails details) {
        super(principal.getAuthorities());
        this.principal = principal;
        super.setAuthenticated(true);
        super.setDetails(details);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
