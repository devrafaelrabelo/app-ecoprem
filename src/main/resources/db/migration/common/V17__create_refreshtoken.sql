CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS refresh_token (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT uq_refresh_user_session UNIQUE (user_id, session_id),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);

-- Índices adicionais para performance
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_token_token ON refresh_token (token);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_session_id ON refresh_token(session_id);