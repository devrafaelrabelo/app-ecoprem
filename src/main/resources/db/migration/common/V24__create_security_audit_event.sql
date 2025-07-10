CREATE SCHEMA IF NOT EXISTS security;
SET search_path TO security;

CREATE TABLE IF NOT EXISTS security_audit_event (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID,
    event_type VARCHAR(255),
    description VARCHAR(255),
    ip_address VARCHAR(255),
    user_agent VARCHAR(255),
    timestamp TIMESTAMP(6),

    -- Novos campos
    path VARCHAR(255),
    method VARCHAR(10),
    username VARCHAR(150),
    user_id_ref UUID,

    CONSTRAINT fk_security_audit_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);