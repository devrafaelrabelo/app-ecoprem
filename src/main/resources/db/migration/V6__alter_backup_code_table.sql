-- Adicionar colunas novas se ainda não existem
ALTER TABLE backup_code
ADD COLUMN IF NOT EXISTS user_id UUID,
ADD COLUMN IF NOT EXISTS used BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS used_at TIMESTAMP;

-- Garantir que "code" tem tamanho suficiente
ALTER TABLE backup_code
ALTER COLUMN code TYPE VARCHAR(255);

-- Se a coluna id não for UUID ainda (e você usava outro tipo), ajusta:
-- ALTER TABLE backup_code
-- ALTER COLUMN id TYPE UUID USING id::uuid;

-- Criar FK se ainda não existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_backup_user'
          AND table_name = 'backup_code'
    ) THEN
        ALTER TABLE backup_code
        ADD CONSTRAINT fk_backup_user FOREIGN KEY (user_id) REFERENCES users(id);
    END IF;
END $$;
