-- Adiciona current_user_id em corporate_phone e internal_extension com relacionamento opcional a users

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'corporate_phone'
        AND column_name = 'current_user_id'
    ) THEN
        ALTER TABLE corporate_phone
        ADD COLUMN current_user_id UUID REFERENCES users(id) ON DELETE SET NULL;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'internal_extension'
        AND column_name = 'current_user_id'
    ) THEN
        ALTER TABLE internal_extension
        ADD COLUMN current_user_id UUID REFERENCES users(id) ON DELETE SET NULL;
    END IF;
END
$$;