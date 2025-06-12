-- V16__create_accesslevel.sql

CREATE TABLE access_level (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    level_number INT,
    description TEXT
);
