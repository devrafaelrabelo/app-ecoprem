CREATE TABLE company (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    cnpj VARCHAR(20) UNIQUE,
    legal_name VARCHAR(150),
    address TEXT,
    active BOOLEAN DEFAULT TRUE
)