SET search_path TO common;

CREATE TABLE IF NOT EXISTS company (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    cnpj VARCHAR(20) UNIQUE,
    legal_name VARCHAR(150),

    -- Campos estruturados de endereço
    street TEXT,
    number TEXT,
    complement TEXT,
    neighborhood TEXT,
    city TEXT,
    state TEXT,
    country TEXT,
    postal_code TEXT,

    active BOOLEAN DEFAULT TRUE
);
