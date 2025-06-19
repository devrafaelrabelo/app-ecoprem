-- Remover as FKs automáticas (caso existam com nomes gerados)
-- Isso precisa ser adaptado conforme seu banco atual, se necessário

-- Adicionar as foreign keys explicitamente

ALTER TABLE resources
ADD CONSTRAINT fk_resource_company
FOREIGN KEY (company_id)
REFERENCES company(id);

ALTER TABLE resources
ADD CONSTRAINT fk_resource_user
FOREIGN KEY (current_user_id)
REFERENCES users(id);

ALTER TABLE resources
ADD CONSTRAINT fk_resource_type
FOREIGN KEY (resource_type_id)
REFERENCES resource_type(id);
