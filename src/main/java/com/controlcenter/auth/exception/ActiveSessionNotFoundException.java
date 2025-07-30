package com.controlcenter.auth.exception;

public class ActiveSessionNotFoundException extends RuntimeException {
    public ActiveSessionNotFoundException(String message) {
        super(message);
    }
}