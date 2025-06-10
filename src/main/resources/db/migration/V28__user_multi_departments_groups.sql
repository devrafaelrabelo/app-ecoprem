-- Nova tabela user_department
CREATE TABLE user_department (
    user_id UUID NOT NULL,
    department_id UUID NOT NULL,
    PRIMARY KEY (user_id, department_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE CASCADE
);

-- Nova tabela user_user_group
CREATE TABLE user_user_group (
    user_id UUID NOT NULL,
    user_group_id UUID NOT NULL,
    PRIMARY KEY (user_id, user_group_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user_group_id) REFERENCES user_group(id) ON DELETE CASCADE
);

ALTER TABLE users DROP COLUMN department_id;
ALTER TABLE users DROP COLUMN group_id;


ALTER TABLE department DROP COLUMN manager_id;
ALTER TABLE user_group DROP COLUMN created_by;