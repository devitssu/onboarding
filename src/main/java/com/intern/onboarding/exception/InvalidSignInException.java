package com.intern.onboarding.exception;

import org.springframework.http.HttpStatus;

public class InvalidSignInException extends CustomException {
    private static final String MESSAGE = "사용자명/비밀번호가 올바르지 않습니다.";

    public InvalidSignInException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
