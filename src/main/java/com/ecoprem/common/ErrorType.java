package com.ecoprem.common;

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
    INVALID_BODY("https://api.ecoprem.com/errors/invalid-body", "Invalid request body");

    private final String uri;
    private final String title;

    ErrorType(String uri, String title) {
        this.uri = uri;
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }
}
