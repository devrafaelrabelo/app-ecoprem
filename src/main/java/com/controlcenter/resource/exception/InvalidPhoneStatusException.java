package com.controlcenter.resource.exception;

public class InvalidPhoneStatusException extends RuntimeException {
    public InvalidPhoneStatusException(String status) {
        super("Status inválido: " + status);
    }
}
