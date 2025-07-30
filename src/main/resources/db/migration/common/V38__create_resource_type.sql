CREATE SCHEMA IF NOT EXISTS resource;
SET search_path TO resource;


CREATE TABLE IF NOT EXISTS resource_type (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);
