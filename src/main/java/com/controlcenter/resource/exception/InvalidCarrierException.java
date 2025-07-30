package com.controlcenter.resource.exception;

public class InvalidCarrierException extends RuntimeException {
    public InvalidCarrierException(String carrier) {
        super("Operadora inválida: " + carrier);
    }
}
