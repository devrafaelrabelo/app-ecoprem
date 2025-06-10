-- Relacionamento created_by -> users (user_group)
ALTER TABLE user_group
ADD CONSTRAINT fk_user_group_created_by
FOREIGN KEY (created_by)
REFERENCES users(id)
ON DELETE SET NULL;

-- Relacionamento manager_id -> users (department)
ALTER TABLE department
ADD CONSTRAINT fk_department_manager
FOREIGN KEY (manager_id)
REFERENCES users(id)
ON DELETE SET NULL;

-- Relacionamentos reversos são tratados pela JPA, não no banco
-- AccessLevel, Department, UserGroup já estão relacionados via User
-- e já existem colunas access_level_id, department_id, group_id na tabela users
-- então nenhum ALTER é necessário no sentido inverso
