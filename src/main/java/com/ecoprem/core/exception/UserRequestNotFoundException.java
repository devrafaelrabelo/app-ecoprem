package com.ecoprem.core.exception;

public class UserRequestNotFoundException extends RuntimeException {
    public UserRequestNotFoundException(String message) {
        super(message);
    }
}