CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS backup_code (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    code VARCHAR(255) NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,

    CONSTRAINT fk_backup_code_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);
