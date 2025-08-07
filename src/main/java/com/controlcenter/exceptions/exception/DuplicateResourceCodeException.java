package com.controlcenter.exceptions.exception;

public class DuplicateResourceCodeException extends RuntimeException {
    public DuplicateResourceCodeException(String message) {
        super(message);
    }
}