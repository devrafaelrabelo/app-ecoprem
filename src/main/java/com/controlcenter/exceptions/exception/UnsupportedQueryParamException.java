package com.controlcenter.exceptions.exception;

public class UnsupportedQueryParamException extends RuntimeException {
    public UnsupportedQueryParamException(String message) {
        super(message);
    }
}