DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='corporate_phone' AND column_name='last_updated'
    ) THEN
        ALTER TABLE corporate_phone ADD COLUMN last_updated TIMESTAMP;
    END IF;
END $$;