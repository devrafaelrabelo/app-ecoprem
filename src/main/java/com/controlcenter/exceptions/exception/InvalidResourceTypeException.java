package com.controlcenter.exceptions.exception;

public class InvalidResourceTypeException extends RuntimeException {
    public InvalidResourceTypeException(String message) {
        super(message);
    }
}