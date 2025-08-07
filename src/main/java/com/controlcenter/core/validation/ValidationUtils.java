package com.controlcenter.core.validation;

import com.controlcenter.exceptions.exception.ResourceNotFoundException;

import java.util.Optional;
import java.util.function.Supplier;

public class ValidationUtils {

    public static <T> T requireFound(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(message));
    }

    public static void require(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!condition) throw exceptionSupplier.get();
    }

    public static void requireNot(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) throw exceptionSupplier.get();
    }
}
