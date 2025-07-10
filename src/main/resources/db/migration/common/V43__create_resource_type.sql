SET search_path TO resource;

-- ===============================
-- Criação da tabela resource_type
-- ===============================
CREATE TABLE IF NOT EXISTS resource_type (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);