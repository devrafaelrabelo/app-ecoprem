CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS password_reset_token (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);
