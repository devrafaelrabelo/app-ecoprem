SET search_path TO common;

CREATE TABLE IF NOT EXISTS company (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    cnpj VARCHAR(20) UNIQUE,
    legal_name VARCHAR(255),
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT true
);
