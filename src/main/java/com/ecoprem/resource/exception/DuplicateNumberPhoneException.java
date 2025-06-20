package com.ecoprem.resource.exception;

import java.util.UUID;

public class DuplicateNumberPhoneException extends RuntimeException {
    public DuplicateNumberPhoneException(String message) {
        super(message);
    }
}
