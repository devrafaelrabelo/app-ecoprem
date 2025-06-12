-- V26__add_user_relationships.sql

-- Tabela de junção: user_department (ManyToMany)
CREATE TABLE IF NOT EXISTS user_department (
    user_id UUID NOT NULL,
    department_id UUID NOT NULL,
    PRIMARY KEY (user_id, department_id),
    CONSTRAINT fk_user_department_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_department_department FOREIGN KEY (department_id) REFERENCES department(id)
);

-- Tabela de junção: user_role (ManyToMany)
CREATE TABLE IF NOT EXISTS user_role (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Tabela de junção: user_function (ManyToMany)
CREATE TABLE IF NOT EXISTS user_function (
    user_id UUID NOT NULL,
    function_id UUID NOT NULL,
    PRIMARY KEY (user_id, function_id),
    CONSTRAINT fk_user_function_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_function_function FOREIGN KEY (function_id) REFERENCES function(id)
);

-- Tabela de junção: user_user_group (ManyToMany)
CREATE TABLE IF NOT EXISTS user_user_group (
    user_id UUID NOT NULL,
    user_group_id UUID NOT NULL,
    PRIMARY KEY (user_id, user_group_id),
    CONSTRAINT fk_user_user_group_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_user_group_group FOREIGN KEY (user_group_id) REFERENCES user_group(id)
);

-- Relacionamento com Position (ManyToOne)
ALTER TABLE users
ADD COLUMN IF NOT EXISTS position_id UUID;

ALTER TABLE users
ADD CONSTRAINT fk_user_position FOREIGN KEY (position_id) REFERENCES position(id);
