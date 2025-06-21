ALTER TABLE security_audit_event
ADD COLUMN path VARCHAR(255);

ALTER TABLE security_audit_event
ADD COLUMN method VARCHAR(10);

ALTER TABLE security_audit_event
ADD COLUMN username VARCHAR(150);

ALTER TABLE security_audit_event
ADD COLUMN user_id_ref UUID;