package com.intern.onboarding.infra.security.jwt;

import com.intern.onboarding.exception.JwtException;
import com.intern.onboarding.infra.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Optional<String> bearerToken = getBearerToken(request);
        bearerToken.ifPresent(token -> {
            Jws<Claims> claims = jwtUtil.getClaims(token);
            if (!TokenType.ACCESS_TOKEN.name().equals(claims.getPayload().get("type")))
                throw new JwtException("AccessToken이 아닙니다.");
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(
                    new UserPrincipal(Long.parseLong(claims.getPayload().getSubject()), Set.of(claims.getPayload().get("role", String.class))),
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        });

        filterChain.doFilter(request, response);
    }

    public static Optional<String> getBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .flatMap(header -> {
                    Pattern pattern = Pattern.compile("^Bearer (.+?)$");
                    Matcher matcher = pattern.matcher(header);
                    return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
                });
    }
}
