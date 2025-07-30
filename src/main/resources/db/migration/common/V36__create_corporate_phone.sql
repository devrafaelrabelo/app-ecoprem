-- Garante o schema correto para esta migration
CREATE SCHEMA IF NOT EXISTS communication;
CREATE SCHEMA IF NOT EXISTS resource;
SET search_path TO communication;

-- =============================
-- Criação dos tipos ENUM com schema explícito
-- =============================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'carrier_type') THEN
        EXECUTE 'CREATE TYPE resource.carrier_type AS ENUM (''VIVO'', ''CLARO'', ''TIM'', ''OI'', ''OUTROS'')';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'plan_type') THEN
        EXECUTE 'CREATE TYPE resource.plan_type AS ENUM (''PREPAID'', ''POSTPAID'', ''CONTROLLED'', ''UNLIMITED'', ''DATA_ONLY'', ''M2M'', ''CORPORATE'', ''CUSTOM'')';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'phone_status') THEN
        EXECUTE 'CREATE TYPE resource.phone_status AS ENUM (''ACTIVE'', ''INACTIVE'', ''LOST'', ''CANCELED'', ''BLOCKED'', ''SUSPENDED'', ''STOLEN'')';
    END IF;
END $$;

-- =============================
-- Criação da tabela corporate_phone
-- =============================
CREATE TABLE IF NOT EXISTS corporate_phone (
    id UUID PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    carrier resource.carrier_type,
    plan_type resource.plan_type,
    status resource.phone_status NOT NULL DEFAULT 'ACTIVE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_updated TIMESTAMP,
    whatsapp_block BOOLEAN NOT NULL DEFAULT FALSE,

    current_user_id UUID REFERENCES security.users(id) ON DELETE SET NULL,
    company_id UUID REFERENCES common.company(id) ON DELETE CASCADE
);
