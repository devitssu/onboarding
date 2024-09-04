package com.intern.onboarding.exception;

import org.springframework.http.HttpStatus;

public class UnAuthorized extends CustomException {

    private static final String MESSAGE = "인증 정보가 올바르지 않습니다.";

    public UnAuthorized() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
