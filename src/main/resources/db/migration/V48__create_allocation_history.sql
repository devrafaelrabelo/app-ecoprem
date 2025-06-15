-- V20250614_08__create_allocation_history.sql

CREATE TABLE IF NOT EXISTS company (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    cnpj VARCHAR(20) UNIQUE,
    legal_name VARCHAR(255),
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT true
);

