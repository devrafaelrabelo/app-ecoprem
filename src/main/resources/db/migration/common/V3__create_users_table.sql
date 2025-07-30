CREATE SCHEMA IF NOT EXISTS security;
SET search_path TO security;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    social_name VARCHAR(255),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    email_verified BOOLEAN,
    password VARCHAR(255) NOT NULL,
    password_last_updated TIMESTAMP,
    account_locked BOOLEAN,
    account_locked_at TIMESTAMP,
    account_deletion_requested BOOLEAN,
    account_deletion_request_date TIMESTAMP,
    origin VARCHAR(255),
    interface_theme VARCHAR(255),
    timezone VARCHAR(255),
    notifications_enabled BOOLEAN,
    login_attempts INT,
    last_password_change_ip VARCHAR(255),
    terms_accepted_at TIMESTAMP,
    privacy_policy_version VARCHAR(255),
    avatar VARCHAR(255),
    cpf VARCHAR(14) UNIQUE,
    birth_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    first_login BOOLEAN,
    preferred_language VARCHAR(255),
    invitation_status VARCHAR(255),
    account_suspended_reason VARCHAR(255),
    last_known_location VARCHAR(255),
    password_compromised BOOLEAN,
    forced_logout_at TIMESTAMP,
    cookie_consent_status VARCHAR(255),
    manager_id UUID,
    two_factor_secret VARCHAR(255),
    two_factor_enabled BOOLEAN,
    status_id UUID,

    -- Endereço embutido (Embedded Address)
    address_street VARCHAR(100),
    address_number VARCHAR(10),
    address_complement VARCHAR(100),
    address_city VARCHAR(100),
    address_neighborhood VARCHAR(100),
    address_state VARCHAR(100),
    address_country VARCHAR(100),
    address_postal_code VARCHAR(20),

    -- Novos campos
    requested_by_id UUID,
    created_by_id UUID,

    -- Chaves estrangeiras
    CONSTRAINT fk_users_status FOREIGN KEY (status_id) REFERENCES user_status(id),
    CONSTRAINT fk_users_requested_by FOREIGN KEY (requested_by_id) REFERENCES users(id),
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- Índice para facilitar login por email
CREATE INDEX IF NOT EXISTS idx_users_email ON security.users(email);
