package com.controlcenter.core.exception;

public class UnsupportedQueryParamException extends RuntimeException {
    public UnsupportedQueryParamException(String message) {
        super(message);
    }
}