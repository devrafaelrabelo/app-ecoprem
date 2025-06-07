-- V17__create_security_audit_event.sql

CREATE TABLE IF NOT EXISTS security_audit_event (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID,
    event_type VARCHAR(255),
    description VARCHAR(255),
    ip_address VARCHAR(255),
    user_agent VARCHAR(255),
    timestamp TIMESTAMP(6),

    CONSTRAINT fk_security_audit_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);
