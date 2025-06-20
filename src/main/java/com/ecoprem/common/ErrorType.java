package com.ecoprem.common;

import lombok.Getter;

@Getter
public enum ErrorType {

    INVALID_CREDENTIALS("https://api.ecoprem.com/errors/invalid-credentials", "Invalid credentials"),
    ACCOUNT_LOCKED("https://api.ecoprem.com/errors/account-locked", "Account locked"),
    ACCOUNT_SUSPENDED("https://api.ecoprem.com/errors/account-suspended", "Account suspended"),
    ACCOUNT_NOT_ACTIVE("https://api.ecoprem.com/errors/account-not-active", "Account not active"),
    USER_NOT_FOUND("https://api.ecoprem.com/errors/user-not-found", "User not found"),
    ROLE_NOT_FOUND("https://api.ecoprem.com/errors/role-not-found", "Role not found"),
    EMAIL_ALREADY_EXISTS("https://api.ecoprem.com/errors/email-exists", "Email already exists"),
    USERNAME_ALREADY_EXISTS("https://api.ecoprem.com/errors/username-exists", "Username already exists"),
    WEAK_PASSWORD("https://api.ecoprem.com/errors/weak-password", "Weak password"),
    INVALID_ROLE_ASSIGNMENT("https://api.ecoprem.com/errors/invalid-role-assignment", "Invalid role assignment"),
    TWO_FACTOR_REQUIRED("https://api.ecoprem.com/errors/2fa-required", "2FA required"),
    INVALID_2FA_TOKEN("https://api.ecoprem.com/errors/invalid-2fa-token", "Invalid 2FA token"),
    EXPIRED_2FA_TOKEN("https://api.ecoprem.com/errors/expired-2fa-token", "Expired 2FA token"),
    INVALID_2FA_CODE("https://api.ecoprem.com/errors/invalid-2fa-code", "Invalid 2FA code"),
    REFRESH_TOKEN_EXPIRED("https://api.ecoprem.com/errors/refresh-token-expired", "Refresh token expired"),
    RATE_LIMIT_EXCEEDED("https://api.ecoprem.com/errors/rate-limit", "Rate limit exceeded"),
    ACCESS_DENIED("https://api.ecoprem.com/errors/access-denied", "Access denied"),
    VALIDATION("https://api.ecoprem.com/errors/validation", "Validation failed"),
    RESOURCE_NOT_FOUND("https://api.ecoprem.com/errors/not-found", "Recurso não encontrado"),
    INVALID_BODY("https://api.ecoprem.com/errors/invalid-body", "Invalid request body"),
    INVALID_TOKEN("https://api.ecoprem.com/errors/invalid-token", "Invalid or expired session"),
    INVALID_RESOURCE_STATUS(
            "https://api.ecoprem.com/errors/invalid-resource-status",
            "Status do recurso inválido ou inexistente"
    ),

    INVALID_COMPANY(
            "https://api.ecoprem.com/errors/invalid-company",
            "Empresa vinculada inválida ou inexistente"
    ),

    INVALID_RESOURCE_TYPE(
            "https://api.ecoprem.com/errors/invalid-resource-type",
            "Tipo de recurso inválido ou inexistente"
    ),

    INVALID_USER(
            "https://api.ecoprem.com/errors/invalid-user",
            "Usuário vinculado inválido ou inexistente"
    ),
    INVALID_CARRIER(
            "https://api.ecoprem.com/errors/invalid-carrier",
            "Operadora inválida fornecida"
    ),
    INVALID_PLAN_TYPE(
            "https://api.ecoprem.com/errors/invalid-plan-type",
            "Tipo de plano inválido fornecido"
    ),
    INVALID_PHONE_STATUS(
            "https://api.ecoprem.com/errors/invalid-phone-status",
            "Status do telefone inválido fornecido"
    ),
    CORPORATE_PHONE_NOT_FOUND(
            "https://api.ecoprem.com/errors/corporate-phone-not-found",
            "Telefone corporativo não encontrado"
    ),
    RELATED_USER_NOT_FOUND(
            "https://api.ecoprem.com/errors/related-user-not-found",
            "Usuário vinculado não encontrado"
    ),
    COMPANY_NOT_FOUND(
            "https://api.ecoprem.com/errors/company-not-found",
            "Empresa não encontrada"
    ),
    DUPLICATE_NUMBER_PHONE(
            "https://api.ecoprem.com/errors/duplicate-number-phone",
            "Número de telefone duplicado"
    ),
    DUPLICATE_RESOURCE_CODE(
            "https://api.ecoprem.com/errors/duplicate-resource-code",
            "Código de recurso duplicado"
    ),
    INVALID_PHONE(
            "https://api.ecoprem.com/errors/invalid-phone",
            "Telefone inválido"
    ),
    INTERNAL_EXTENSION_ERROR("/errors/internal-extension/not-found", "Erro no processo interno"),;



    private final String uri;
    private final String title;

    ErrorType(String uri, String title) {
        this.uri = uri;
        this.title = title;
    }
}
