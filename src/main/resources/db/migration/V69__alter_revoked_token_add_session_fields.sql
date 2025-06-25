ALTER TABLE revoked_token
ADD COLUMN session_id VARCHAR(255);

ALTER TABLE revoked_token
ADD COLUMN reason VARCHAR(255);

ALTER TABLE revoked_token
ADD COLUMN revoked_by VARCHAR(255);
