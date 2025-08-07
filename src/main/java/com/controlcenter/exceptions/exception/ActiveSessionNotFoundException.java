package com.controlcenter.exceptions.exception;

public class ActiveSessionNotFoundException extends RuntimeException {
    public ActiveSessionNotFoundException(String message) {
        super(message);
    }
}