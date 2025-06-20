package com.ecoprem.resource.exception;

import java.util.UUID;

public class CorporatePhoneNotFoundException extends RuntimeException {
    public CorporatePhoneNotFoundException(String message) {
        super(message);
    }
}
