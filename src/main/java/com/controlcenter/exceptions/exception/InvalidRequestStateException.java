package com.controlcenter.exceptions.exception;

public class InvalidRequestStateException extends RuntimeException {
    public InvalidRequestStateException(String message) {
        super(message);
    }
}