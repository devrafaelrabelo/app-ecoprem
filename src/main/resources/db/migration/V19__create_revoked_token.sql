-- V13__create_revoked_token.sql

CREATE TABLE revoked_token (
    id UUID PRIMARY KEY,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NOT NULL,
    user_id UUID,

    CONSTRAINT fk_revoked_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);