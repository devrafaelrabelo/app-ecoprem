-- Remove a foreign key da tabela users
ALTER TABLE users DROP CONSTRAINT IF EXISTS fk_users_access_level;

-- Remove a coluna access_level_id da tabela users
ALTER TABLE users DROP COLUMN IF EXISTS access_level_id;

-- Remove a tabela access_level
DROP TABLE IF EXISTS access_level;
