CREATE SCHEMA IF NOT EXISTS integration;
SET search_path TO integration;

CREATE TABLE IF NOT EXISTS webhook_log (
    id UUID PRIMARY KEY,
    webhook_id UUID NOT NULL REFERENCES integration.webhook(id) ON DELETE CASCADE,
    request_payload TEXT,
    response_status INT,
    response_body TEXT,
    attempt_time TIMESTAMP DEFAULT NOW(),
    success BOOLEAN,
    error_message TEXT
);
