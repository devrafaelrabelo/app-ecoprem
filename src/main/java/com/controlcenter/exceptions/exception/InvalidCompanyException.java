package com.controlcenter.exceptions.exception;

public class InvalidCompanyException extends RuntimeException {
    public InvalidCompanyException(String message) {
        super(message);
    }
}