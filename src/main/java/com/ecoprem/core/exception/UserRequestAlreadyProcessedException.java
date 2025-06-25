package com.ecoprem.core.exception;

public class UserRequestAlreadyProcessedException extends RuntimeException {
    public UserRequestAlreadyProcessedException(String message) {
        super(message);
    }
}