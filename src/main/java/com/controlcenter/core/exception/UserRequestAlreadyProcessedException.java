package com.controlcenter.core.exception;

public class UserRequestAlreadyProcessedException extends RuntimeException {
    public UserRequestAlreadyProcessedException(String message) {
        super(message);
    }
}