ALTER TABLE request_audit_log
ADD COLUMN user_id UUID;

ALTER TABLE request_audit_log
ADD CONSTRAINT fk_request_log_user
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE request_audit_log
ADD COLUMN user_id_ref UUID;