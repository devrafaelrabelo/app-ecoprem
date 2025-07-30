package com.controlcenter.auth.exception;

public class Invalid2FACodeException extends RuntimeException {
    public Invalid2FACodeException(String message) {
        super(message);
    }
}
