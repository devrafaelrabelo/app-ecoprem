package com.ecoprem.core.exception;

public class DuplicatePermissionNameException extends RuntimeException {
    public DuplicatePermissionNameException(String message) {
        super(message);
    }
}