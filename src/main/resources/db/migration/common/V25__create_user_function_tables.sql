CREATE SCHEMA IF NOT EXISTS common;
SET search_path TO common;

-- Tabela de funções por departamento
CREATE TABLE IF NOT EXISTS function (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    department_id UUID,
    CONSTRAINT fk_function_department FOREIGN KEY (department_id)
         REFERENCES common.department (id)
         ON DELETE SET NULL
);

-- Tabela de junção: user_function (ManyToMany)
CREATE TABLE IF NOT EXISTS user_function (
    user_id UUID NOT NULL,
    function_id UUID NOT NULL,
    PRIMARY KEY (user_id, function_id),
    CONSTRAINT fk_user_function_user FOREIGN KEY (user_id) REFERENCES security.users(id),
    CONSTRAINT fk_user_function_function FOREIGN KEY (function_id) REFERENCES common.function(id)
);
