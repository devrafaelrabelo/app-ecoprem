package com.controlcenter.auth.exception;

public class PasswordTooWeakException extends RuntimeException {
    public PasswordTooWeakException(String message) {
        super(message);
    }
}
