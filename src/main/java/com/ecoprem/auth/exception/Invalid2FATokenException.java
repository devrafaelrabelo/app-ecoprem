package com.ecoprem.auth.exception;

public class Invalid2FATokenException extends RuntimeException {
    public Invalid2FATokenException(String message) {
        super(message);
    }
}
