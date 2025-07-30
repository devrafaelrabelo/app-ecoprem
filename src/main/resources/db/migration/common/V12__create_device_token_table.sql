CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS device_token (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    device_name VARCHAR(255),
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_device_token_user FOREIGN KEY (user_id) REFERENCES security.users(id)

 );
