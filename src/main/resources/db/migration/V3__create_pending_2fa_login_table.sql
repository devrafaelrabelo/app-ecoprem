CREATE TABLE pending2falogin (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    temp_token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);