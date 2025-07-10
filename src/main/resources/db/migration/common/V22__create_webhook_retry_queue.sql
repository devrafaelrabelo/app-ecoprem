CREATE SCHEMA IF NOT EXISTS security;
SET search_path TO security;

CREATE TABLE IF NOT EXISTS webhook_retry_queue (
    id UUID PRIMARY KEY,
    webhook_id UUID NOT NULL REFERENCES security.webhook(id) ON DELETE CASCADE,
    payload TEXT NOT NULL,
    retries INT DEFAULT 0,
    next_attempt_at TIMESTAMP NOT NULL,
    last_error TEXT
);
