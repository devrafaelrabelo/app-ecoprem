CREATE TABLE request_audit_log (
    id SERIAL PRIMARY KEY,
    method VARCHAR(10),
    path VARCHAR(255),
    ip_address VARCHAR(50),
    status_code INT,
    user_agent VARCHAR(1000),
    timestamp TIMESTAMP
);
ss