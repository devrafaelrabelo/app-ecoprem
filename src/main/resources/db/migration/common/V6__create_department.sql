CREATE SCHEMA IF NOT EXISTS common;
SET search_path TO common;

CREATE TABLE IF NOT EXISTS department (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    manager_id UUID,

    CONSTRAINT fk_department_manager
        FOREIGN KEY (manager_id)
        REFERENCES security.users(id)
        ON DELETE SET NULL
);