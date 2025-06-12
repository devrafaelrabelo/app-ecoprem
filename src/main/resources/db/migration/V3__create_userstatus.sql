-- V3__create_userstatus.sql

CREATE TABLE user_status (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN
);