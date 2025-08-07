package com.controlcenter.exceptions.exception;

public class InvalidCarrierException extends RuntimeException {
    public InvalidCarrierException(String carrier) {
        super("Operadora inválida: " + carrier);
    }
}
