-- V6__create_backup_code_table.sql

CREATE TABLE backup_code (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    code VARCHAR(255) NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,

    CONSTRAINT fk_backup_code_user FOREIGN KEY (user_id) REFERENCES users(id)
);
