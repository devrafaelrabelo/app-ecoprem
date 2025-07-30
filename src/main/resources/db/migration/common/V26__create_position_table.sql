CREATE SCHEMA IF NOT EXISTS common;
SET search_path TO common;

CREATE TABLE IF NOT EXISTS position (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);
