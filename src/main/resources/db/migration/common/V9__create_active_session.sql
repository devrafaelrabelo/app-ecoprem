CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS active_session (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    device VARCHAR(255),
    browser VARCHAR(255),
    operating_system VARCHAR(255),
    ip_address VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    last_access_at TIMESTAMP,

    CONSTRAINT fk_active_session_user FOREIGN KEY (user_id)
        REFERENCES security.users(id)
);