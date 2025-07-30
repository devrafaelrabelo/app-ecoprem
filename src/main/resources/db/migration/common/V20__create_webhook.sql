CREATE SCHEMA IF NOT EXISTS integration;
SET search_path TO integration;

CREATE TABLE IF NOT EXISTS webhook (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    url TEXT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    secret TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    last_called_at TIMESTAMP
);
