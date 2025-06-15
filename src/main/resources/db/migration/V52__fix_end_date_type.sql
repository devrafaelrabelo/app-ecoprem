-- V52__fix_end_date_type.sql
ALTER TABLE allocation_history
ALTER COLUMN end_date TYPE DATE USING end_date::DATE;