package com.controlcenter.exceptions.exception;

public class UserRequestAlreadyProcessedException extends RuntimeException {
    public UserRequestAlreadyProcessedException(String message) {
        super(message);
    }
}