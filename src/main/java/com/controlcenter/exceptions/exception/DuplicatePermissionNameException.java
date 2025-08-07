package com.controlcenter.exceptions.exception;

public class DuplicatePermissionNameException extends RuntimeException {
    public DuplicatePermissionNameException(String message) {
        super(message);
    }
}