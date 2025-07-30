CREATE SCHEMA IF NOT EXISTS "user";
SET search_path TO "user";

CREATE TABLE IF NOT EXISTS privacy_settings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    share_activity BOOLEAN DEFAULT FALSE,
    show_online_status BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_privacy_settings_user FOREIGN KEY (user_id) REFERENCES security.users(id)
);
