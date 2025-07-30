CREATE SCHEMA IF NOT EXISTS communication;
SET search_path TO communication;

CREATE TABLE IF NOT EXISTS personal_phone (
    id UUID PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    type VARCHAR(20),
    user_id UUID NOT NULL,

    CONSTRAINT fk_personal_phone_user FOREIGN KEY (user_id)
        REFERENCES security.users(id)
        ON DELETE CASCADE
);
