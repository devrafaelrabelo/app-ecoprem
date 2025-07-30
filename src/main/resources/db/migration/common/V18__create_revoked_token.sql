CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS revoked_token (
    id UUID PRIMARY KEY,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NOT NULL,
    user_id UUID,

    -- Novos campos incorporados
    session_id VARCHAR(255),
    reason VARCHAR(255),
    revoked_by VARCHAR(255),

    CONSTRAINT fk_revoked_token_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);