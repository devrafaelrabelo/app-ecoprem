package com.controlcenter.exceptions.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(String.valueOf(message));
    }
}
