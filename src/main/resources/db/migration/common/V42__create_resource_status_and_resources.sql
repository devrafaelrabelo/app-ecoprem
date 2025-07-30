SET search_path TO resource;

-- ========================================
-- TABELA: resource_status
-- ========================================
CREATE TABLE IF NOT EXISTS resource_status (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    blocks_allocation BOOLEAN NOT NULL DEFAULT FALSE
);

-- ========================================
-- TABELA: resources
-- ========================================
CREATE TABLE IF NOT EXISTS resources (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,

    asset_tag VARCHAR(100),             -- Código patrimonial interno
    serial_number VARCHAR(100),         -- Número de série do fabricante
    brand VARCHAR(100),
    model VARCHAR(100),
    price DECIMAL(10, 2),
    purchase_date DATE,
    warranty_end_date DATE,

    location VARCHAR(150),
    responsible_sector VARCHAR(100),

    -- ADICIONE AS COLUNAS USADAS EM FKs
    company_id UUID,
    current_user_id UUID,
    resource_type_id UUID,
    status_id UUID,

    -- CONSTRAINTS APÓS AS COLUNAS EXISTIREM
    CONSTRAINT fk_resource_company FOREIGN KEY (company_id) REFERENCES common.company(id),
    CONSTRAINT fk_resource_user FOREIGN KEY (current_user_id) REFERENCES security.users(id),
    CONSTRAINT fk_resource_type FOREIGN KEY (resource_type_id) REFERENCES resource.resource_type(id),
    CONSTRAINT fk_resource_status FOREIGN KEY (status_id) REFERENCES resource.resource_status(id),

    available_for_use BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
