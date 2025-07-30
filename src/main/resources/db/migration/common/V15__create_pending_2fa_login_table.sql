CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS pending_2fa_login (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    temp_token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_pending2fa_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);
