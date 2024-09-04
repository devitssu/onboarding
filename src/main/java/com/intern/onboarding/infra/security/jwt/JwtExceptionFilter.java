package com.intern.onboarding.infra.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intern.onboarding.exception.CustomException;
import com.intern.onboarding.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            int statusCode = e.getStatusCode();

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(statusCode);

            ErrorResponse error = new ErrorResponse(String.valueOf(statusCode), e.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
        }
    }
}
