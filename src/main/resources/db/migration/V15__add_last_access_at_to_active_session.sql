ALTER TABLE active_session
ADD COLUMN last_access_at TIMESTAMP;

UPDATE active_session
SET last_access_at = created_at
WHERE last_access_at IS NULL;