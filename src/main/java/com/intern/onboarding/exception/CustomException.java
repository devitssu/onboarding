package com.intern.onboarding.exception;

public abstract class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }

    public abstract int getStatusCode();
}
