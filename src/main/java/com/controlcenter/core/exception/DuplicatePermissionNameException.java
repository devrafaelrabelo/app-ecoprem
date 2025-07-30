package com.controlcenter.core.exception;

public class DuplicatePermissionNameException extends RuntimeException {
    public DuplicatePermissionNameException(String message) {
        super(message);
    }
}