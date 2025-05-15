DROP TABLE revoked_token;

CREATE TABLE revoked_token (
    id UUID PRIMARY KEY,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    user_id UUID
);