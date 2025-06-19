-- ========================================
-- TABELA: resource_status
-- ========================================
CREATE TABLE IF NOT EXISTS resource_status (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,           -- Ex: 'DISPONIVEL', 'ALOCADO'
    name VARCHAR(100) NOT NULL,                 -- Ex: 'Disponível', 'Em uso'
    description TEXT,
    blocks_allocation BOOLEAN DEFAULT FALSE     -- Se true, impede novas alocações
);

-- ========================================
-- TABELA: resources
-- ========================================
CREATE TABLE resources (
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

    company_id UUID REFERENCES company(id) ON DELETE SET NULL,
    current_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    resource_type_id UUID REFERENCES resource_type(id) ON DELETE SET NULL,
    status_id UUID REFERENCES resource_status(id) ON DELETE SET NULL,

    available_for_use BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- ========================================
-- DADOS INICIAIS: resource_status
-- ========================================
INSERT INTO resource_status (id, code, name, description, blocks_allocation) VALUES
  ('00000000-0000-0000-0000-000000000001', 'DISPONIVEL', 'Disponível', 'Recurso pronto para ser alocado', FALSE),
  ('00000000-0000-0000-0000-000000000002', 'ALOCADO', 'Alocado', 'Recurso em uso por um colaborador', TRUE),
  ('00000000-0000-0000-0000-000000000003', 'MANUTENCAO', 'Em manutenção', 'Recurso fora de uso por manutenção', TRUE),
  ('00000000-0000-0000-0000-000000000004', 'DESCARTADO', 'Descartado', 'Recurso aposentado, inutilizado ou doado', TRUE),
  ('00000000-0000-0000-0000-000000000005', 'PERDIDO', 'Perdido', 'Recurso extraviado ou não localizado', TRUE),
  ('00000000-0000-0000-0000-000000000006', 'BLOQUEADO', 'Bloqueado', 'Indisponível temporariamente ou por processo interno', TRUE);
