package com.ecoprem.resource.exception;

public class InvalidResourceTypeException extends RuntimeException {
    public InvalidResourceTypeException(String message) {
        super(message);
    }
}