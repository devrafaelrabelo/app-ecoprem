package com.controlcenter.resource.exception;

public class InvalidResourceStatusException extends RuntimeException {
    public InvalidResourceStatusException(String message) {
        super(message);
    }
}