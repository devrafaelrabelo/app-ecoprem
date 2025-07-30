CREATE SCHEMA IF NOT EXISTS audit;
SET search_path TO audit;

CREATE TABLE request_audit_log (
    id BIGSERIAL PRIMARY KEY,
    method VARCHAR(10),
    path VARCHAR(255),
    ip_address VARCHAR(50),
    status_code INT,
    user_agent VARCHAR(1000),
    timestamp TIMESTAMP,
    username VARCHAR(150),
    duration_ms INT,
    user_id UUID,
    user_id_ref UUID,

    CONSTRAINT fk_request_log_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);

