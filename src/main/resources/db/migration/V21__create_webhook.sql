-- V14__create_webhook.sql

CREATE TABLE webhook (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    url TEXT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    secret TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    last_called_at TIMESTAMP
);
