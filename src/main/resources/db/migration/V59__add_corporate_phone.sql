-- Criar os novos tipos ENUM
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

-- Adicionar a coluna status se ainda não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'corporate_phone' AND column_name = 'status'
    ) THEN
        ALTER TABLE corporate_phone ADD COLUMN status phone_status DEFAULT 'ACTIVE' NOT NULL;
    END IF;
END $$;

-- Fazer o mesmo para carrier e plan_type (opcional se já existirem)
-- Apenas convertendo o tipo (não adicionando a coluna, presumindo que já existem)

ALTER TABLE corporate_phone
    ALTER COLUMN carrier TYPE carrier_type USING carrier::carrier_type,
    ALTER COLUMN plan_type TYPE plan_type USING plan_type::plan_type;
