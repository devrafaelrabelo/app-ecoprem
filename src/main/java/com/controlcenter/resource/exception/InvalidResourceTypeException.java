package com.controlcenter.resource.exception;

public class InvalidResourceTypeException extends RuntimeException {
    public InvalidResourceTypeException(String message) {
        super(message);
    }
}