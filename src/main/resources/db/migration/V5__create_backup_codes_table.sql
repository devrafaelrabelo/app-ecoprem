ALTER TABLE backup_code (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    code VARCHAR(255) NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL,
    CONSTRAINT fk_backup_user FOREIGN KEY (user_id) REFERENCES users(id)
);