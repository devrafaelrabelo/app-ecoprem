-- V7__create_device_token_table.sql

CREATE TABLE device_token (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    device_name VARCHAR(255),
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,

    CONSTRAINT fk_device_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);