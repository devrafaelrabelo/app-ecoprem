package com.controlcenter.exceptions.enums;

import lombok.Getter;

@Getter
public enum ErrorType {

    INVALID_CREDENTIALS("https://api.controlcenter.com/errors/invalid-credentials", "Invalid credentials"),
    ACCOUNT_LOCKED("https://api.controlcenter.com/errors/account-locked", "Account locked"),
    ACCOUNT_SUSPENDED("https://api.controlcenter.com/errors/account-suspended", "Account suspended"),
    ACCOUNT_NOT_ACTIVE("https://api.controlcenter.com/errors/account-not-active", "Account not active"),
    USER_NOT_FOUND("https://api.controlcenter.com/errors/user-not-found", "User not found"),
    ROLE_NOT_FOUND("https://api.controlcenter.com/errors/role-not-found", "Role not found"),
    EMAIL_ALREADY_EXISTS("https://api.controlcenter.com/errors/email-exists", "Email already exists"),
    USERNAME_ALREADY_EXISTS("https://api.controlcenter.com/errors/username-exists", "Username already exists"),
    WEAK_PASSWORD("https://api.controlcenter.com/errors/weak-password", "Weak password"),
    INVALID_ROLE_ASSIGNMENT("https://api.controlcenter.com/errors/invalid-role-assignment", "Invalid role assignment"),
    TWO_FACTOR_REQUIRED("https://api.controlcenter.com/errors/2fa-required", "2FA required"),
    INVALID_2FA_TOKEN("https://api.controlcenter.com/errors/invalid-2fa-token", "Invalid 2FA token"),
    EXPIRED_2FA_TOKEN("https://api.controlcenter.com/errors/expired-2fa-token", "Expired 2FA token"),
    INVALID_2FA_CODE("https://api.controlcenter.com/errors/invalid-2fa-code", "Invalid 2FA code"),
    REFRESH_TOKEN_EXPIRED("https://api.controlcenter.com/errors/refresh-token-expired", "Refresh token expired"),
    RATE_LIMIT_EXCEEDED("https://api.controlcenter.com/errors/rate-limit", "Rate limit exceeded"),
    ACCESS_DENIED("https://api.controlcenter.com/errors/access-denied", "Access denied"),
    VALIDATION("https://api.controlcenter.com/errors/validation", "Validation failed"),
    RESOURCE_NOT_FOUND("https://api.controlcenter.com/errors/not-found", "Recurso não encontrado"),
    INVALID_BODY("https://api.controlcenter.com/errors/invalid-body", "Invalid request body"),
    INVALID_TOKEN("https://api.controlcenter.com/errors/invalid-token", "Invalid or expired session"),
    PERMISSION_NOT_FOUND("https://api.controlcenter.com/errors/permission-not-found", "Permissão não encontrada"),
    CONFLICT("https://api.controlcenter.com/errors/conflict", "Conflito de dados"),
    INVALID_RESOURCE_STATUS(
            "https://api.controlcenter.com/errors/invalid-resource-status",
            "Status do recurso inválido ou inexistente"
    ),

    INVALID_COMPANY(
            "https://api.controlcenter.com/errors/invalid-company",
            "Empresa vinculada inválida ou inexistente"
    ),

    INVALID_RESOURCE_TYPE(
            "https://api.controlcenter.com/errors/invalid-resource-type",
            "Tipo de recurso inválido ou inexistente"
    ),

    INVALID_USER(
            "https://api.controlcenter.com/errors/invalid-user",
            "Usuário vinculado inválido ou inexistente"
    ),
    INVALID_CARRIER(
            "https://api.controlcenter.com/errors/invalid-carrier",
            "Operadora inválida fornecida"
    ),
    INVALID_PLAN_TYPE(
            "https://api.controlcenter.com/errors/invalid-plan-type",
            "Tipo de plano inválido fornecido"
    ),
    INVALID_PHONE_STATUS(
            "https://api.controlcenter.com/errors/invalid-phone-status",
            "Status do telefone inválido fornecido"
    ),
    CORPORATE_PHONE_NOT_FOUND(
            "https://api.controlcenter.com/errors/corporate-phone-not-found",
            "Telefone corporativo não encontrado"
    ),
    RELATED_USER_NOT_FOUND(
            "https://api.controlcenter.com/errors/related-user-not-found",
            "Usuário vinculado não encontrado"
    ),
    COMPANY_NOT_FOUND(
            "https://api.controlcenter.com/errors/company-not-found",
            "Empresa não encontrada"
    ),
    DUPLICATE_NUMBER_PHONE(
            "https://api.controlcenter.com/errors/duplicate-number-phone",
            "Número de telefone duplicado"
    ),
    DUPLICATE_RESOURCE_CODE(
            "https://api.controlcenter.com/errors/duplicate-resource-code",
            "Código de recurso duplicado"
    ),
    INVALID_PHONE(
            "https://api.controlcenter.com/errors/invalid-phone",
            "Telefone inválido"
    ),
    INTERNAL_EXTENSION_ERROR(
            "/errors/internal-extension/not-found",
            "Erro no processo interno"
    ),
    INVALID_PARAMETERS(
            "https://api.controlcenter.com/errors/invalid-parameters",
            "Parâmetros inválidos"
    ),
    UNSUPPORTED_QUERY_PARAMETER(
            "https://api.controlcenter.com/errors/unsupported-query-parameter",
            "Parâmetro de consulta não suportado"
    ),
    DATE_RANGE_INVALID(
            "https://api.controlcenter.com/errors/invalid-date-range",
            "Intervalo de datas inválido"
    ),
    ACTIVE_SESSION_NOT_FOUND(
            "https://api.controlcenter.com/errors/active-session-not-found",
            "Sessão ativa não encontrada"
    ),
    USERREQUEST_NOT_FOUND(
            "https://api.controlcenter.com/errors/userrequest-not-found",
            "Solicitação de usuário não encontrada"
    ),
    OPERATION_NOT_ALLOWED(
            "https://api.controlcenter.com/errors/operation-not-allowed",
            "Operação não permitida"
    ),
    FORBIDDEN_OPERATION(
            "https://api.controlcenter.com/errors/forbidden-operation",
            "Operação proibida"
    ),
    INVALID_STATE(
            "https://api.controlcenter.com/errors/invalid-state",
            "Estado inválido fornecido"
    );


    private final String uri;
    private final String title;

    ErrorType(String uri, String title) {
        this.uri = uri;
        this.title = title;
    }
}
