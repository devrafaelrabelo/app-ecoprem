package com.controlcenter.resource.exception;

public class DuplicateResourceCodeException extends RuntimeException {
    public DuplicateResourceCodeException(String message) {
        super(message);
    }
}