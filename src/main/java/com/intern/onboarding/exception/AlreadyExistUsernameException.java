package com.intern.onboarding.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistUsernameException extends CustomException {

    private static final String MESSAGE = "이미 가입된 사용자명입니다.";

    public AlreadyExistUsernameException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
