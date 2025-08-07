package com.controlcenter.exceptions.exception;

public class InvalidResourceStatusException extends RuntimeException {
    public InvalidResourceStatusException(String message) {
        super(message);
    }
}