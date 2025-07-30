package com.controlcenter.core.exception;

public class InvalidPermissionException extends RuntimeException {
    public InvalidPermissionException(String message) {
        super(message);
    }
}