package com.intern.onboarding.exception;

import org.springframework.http.HttpStatus;

public class AccessDenied extends CustomException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public AccessDenied() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
