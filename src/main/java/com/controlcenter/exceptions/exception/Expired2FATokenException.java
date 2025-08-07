package com.controlcenter.exceptions.exception;

public class Expired2FATokenException extends RuntimeException {
    public Expired2FATokenException(String message) {
        super(message);
    }
}
