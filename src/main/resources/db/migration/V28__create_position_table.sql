-- VXX__create_position_table.sql
CREATE TABLE position (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);
