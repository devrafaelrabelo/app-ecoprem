DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'allocation_history' AND column_name = 'start_date'
    ) THEN
        ALTER TABLE allocation_history ADD COLUMN start_date DATE;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'allocation_history' AND column_name = 'end_date'
    ) THEN
        ALTER TABLE allocation_history ADD COLUMN end_date DATE;
    END IF;
END
$$;