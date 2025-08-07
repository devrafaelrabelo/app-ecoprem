package com.controlcenter.exceptions.exception;

public class TwoFactorRequiredException extends RuntimeException {
    private final String tempToken;

    public TwoFactorRequiredException(String message, String tempToken) {
        super(message);
        this.tempToken = tempToken;
    }

    public String getTempToken() {
        return tempToken;
    }
}
