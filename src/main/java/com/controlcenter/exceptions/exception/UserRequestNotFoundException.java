package com.controlcenter.exceptions.exception;

public class UserRequestNotFoundException extends RuntimeException {
    public UserRequestNotFoundException(String message) {
        super(message);
    }
}