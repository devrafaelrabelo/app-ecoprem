-- V11__create_privacy_settings.sql

CREATE TABLE privacy_settings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    share_activity BOOLEAN DEFAULT FALSE,
    show_online_status BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_privacy_settings_user FOREIGN KEY (user_id) REFERENCES users(id)
);
