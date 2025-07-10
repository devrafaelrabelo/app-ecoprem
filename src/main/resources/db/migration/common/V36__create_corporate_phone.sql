SET search_path TO resource;

-- =============================
-- Criação dos tipos ENUM
-- =============================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'carrier_type') THEN
        CREATE TYPE carrier_type AS ENUM ('VIVO', 'CLARO', 'TIM', 'OI', 'OUTROS');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'plan_type') THEN
        CREATE TYPE plan_type AS ENUM ('PREPAID', 'POSTPAID', 'CONTROLLED', 'UNLIMITED', 'DATA_ONLY', 'M2M', 'CORPORATE', 'CUSTOM');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'phone_status') THEN
        CREATE TYPE phone_status AS ENUM ('ACTIVE', 'INACTIVE', 'LOST', 'CANCELED', 'BLOCKED', 'SUSPENDED', 'STOLEN');
    END IF;
END $$;

-- =============================
-- Criação da tabela corporate_phone
-- =============================
CREATE TABLE IF NOT EXISTS corporate_phone (
    id UUID PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    carrier carrier_type,
    plan_type plan_type,
    status phone_status NOT NULL DEFAULT 'ACTIVE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_updated TIMESTAMP,

    current_user_id UUID REFERENCES security.users(id) ON DELETE SET NULL,
    company_id UUID REFERENCES security.company(id) ON DELETE CASCADE
);
