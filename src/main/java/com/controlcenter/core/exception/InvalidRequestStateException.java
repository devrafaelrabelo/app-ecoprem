package com.controlcenter.core.exception;

public class InvalidRequestStateException extends RuntimeException {
    public InvalidRequestStateException(String message) {
        super(message);
    }
}