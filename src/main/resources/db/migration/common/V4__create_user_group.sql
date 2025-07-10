CREATE SCHEMA IF NOT EXISTS security;
SET search_path TO security;

CREATE TABLE IF NOT EXISTS user_group (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_by UUID,

    CONSTRAINT fk_user_group_created_by FOREIGN KEY (created_by) REFERENCES security.users(id)
);