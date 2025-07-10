CREATE SCHEMA IF NOT EXISTS security;
SET search_path TO security;

-- Tabela de funções por departamento
CREATE TABLE IF NOT EXISTS function (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    department_id UUID,
    CONSTRAINT fk_function_department FOREIGN KEY (department_id)
        REFERENCES security.department(id)
);

-- Associação N:N entre usuários e funções
CREATE TABLE IF NOT EXISTS user_function (
    user_id UUID NOT NULL,
    function_id UUID NOT NULL,
    PRIMARY KEY (user_id, function_id),
    CONSTRAINT fk_user_function_user FOREIGN KEY (user_id)
        REFERENCES security.users(id),
    CONSTRAINT fk_user_function_function FOREIGN KEY (function_id)
        REFERENCES security.function(id)
);
