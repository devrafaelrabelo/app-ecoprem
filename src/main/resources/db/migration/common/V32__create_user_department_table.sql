SET search_path TO "user";

CREATE TABLE IF NOT EXISTS user_department (
    user_id UUID NOT NULL,
    department_id UUID NOT NULL,
    PRIMARY KEY (user_id, department_id),
    CONSTRAINT fk_user_department_user FOREIGN KEY (user_id) REFERENCES security.users(id),
    CONSTRAINT fk_user_department_department FOREIGN KEY (department_id) REFERENCES common.department(id)
);
