CREATE TABLE revoked_token (
    id UUID PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id UUID,
    CONSTRAINT fk_revoked_token_user FOREIGN KEY (user_id) REFERENCES users (id)
);