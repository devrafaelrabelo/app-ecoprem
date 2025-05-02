package com.ecoprem.auth.exception;

public class PasswordTooWeakException extends RuntimeException {
    public PasswordTooWeakException(String message) {
        super(message);
    }
}
