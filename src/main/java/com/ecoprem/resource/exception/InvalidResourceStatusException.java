package com.ecoprem.resource.exception;

public class InvalidResourceStatusException extends RuntimeException {
    public InvalidResourceStatusException(String message) {
        super(message);
    }
}