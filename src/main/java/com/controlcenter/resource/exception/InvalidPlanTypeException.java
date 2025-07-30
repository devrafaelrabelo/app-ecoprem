package com.controlcenter.resource.exception;

public class InvalidPlanTypeException extends RuntimeException {
    public InvalidPlanTypeException(String planType) {
        super("Tipo de plano inválido: " + planType);
    }
}
