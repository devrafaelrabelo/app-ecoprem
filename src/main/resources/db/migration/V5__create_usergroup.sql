-- V2__create_user_group_table.sql


CREATE TABLE user_group (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_by UUID,

    CONSTRAINT fk_user_group_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);