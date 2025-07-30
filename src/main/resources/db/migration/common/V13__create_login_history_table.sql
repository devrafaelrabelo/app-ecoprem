CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS login_history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    login_date TIMESTAMP NOT NULL,
    ip_address VARCHAR(255),
    location VARCHAR(255),
    device VARCHAR(255),
    browser VARCHAR(255),
    operating_system VARCHAR(255),
    success BOOLEAN,
    failure_reason VARCHAR(500),

    CONSTRAINT fk_login_history_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);
