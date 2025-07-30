CREATE SCHEMA IF NOT EXISTS "user";
SET search_path TO "user";

CREATE TABLE IF NOT EXISTS user_department (
    user_id UUID NOT NULL,
    department_id UUID NOT NULL,
    PRIMARY KEY (user_id, department_id),
    FOREIGN KEY (user_id) REFERENCES security.users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES common.department(id) ON DELETE CASCADE
);
