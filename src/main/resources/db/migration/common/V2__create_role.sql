SET search_path TO security;

CREATE TABLE IF NOT EXISTS role (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_system_role BOOLEAN DEFAULT FALSE
);
