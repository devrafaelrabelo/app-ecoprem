package com.controlcenter.auth.util;

public class ValidationUtil {

    /**
     * Valida se o email está num formato válido (simples regex).
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * Valida se a senha é forte:
     * - Mínimo 8 caracteres
     * - Pelo menos 1 letra maiúscula
     * - Pelo menos 1 letra minúscula
     * - Pelo menos 1 número
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        return hasUpper && hasLower && hasNumber;
    }

    // Podemos adicionar mais validações depois, ex: isValidUsername(), etc.
}
