package com.intern.onboarding.exception;

import org.springframework.http.HttpStatus;

public class JwtException extends CustomException {

    private String message;

    public JwtException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
