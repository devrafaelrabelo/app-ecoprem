package com.controlcenter.exceptions.exception;

public class PasswordTooWeakException extends RuntimeException {
    public PasswordTooWeakException(String message) {
        super(message);
    }
}
