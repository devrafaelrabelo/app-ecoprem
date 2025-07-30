CREATE SCHEMA IF NOT EXISTS auth;
SET search_path TO auth;

CREATE TABLE IF NOT EXISTS activity_log (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    activity VARCHAR(255) NOT NULL,
    activity_date TIMESTAMP NOT NULL,
    ip_address VARCHAR(255),
    location VARCHAR(255),
    target_user_id UUID,

    CONSTRAINT fk_activity_log_user FOREIGN KEY (user_id) REFERENCES security.users(id),
    CONSTRAINT fk_activity_log_target_user FOREIGN KEY (target_user_id) REFERENCES security.users(id)
);
