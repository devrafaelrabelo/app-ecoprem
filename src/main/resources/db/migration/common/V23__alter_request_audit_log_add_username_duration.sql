SET search_path TO audit;

ALTER TABLE request_audit_log
ADD COLUMN IF NOT EXISTS username VARCHAR(150);

ALTER TABLE request_audit_log
ADD COLUMN IF NOT EXISTS duration_ms INT;
