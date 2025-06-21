-- Garante que a coluna exista (caso já tenha sido criada)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'request_audit_log'
        AND column_name = 'user_id_ref'
    ) THEN
        ALTER TABLE request_audit_log ADD COLUMN user_id_ref UUID;
    END IF;
END $$;

-- Remove qualquer restrição que impeça atualização direta (se necessário)
-- Aqui deixamos como simples campo UUID sem restrição automática
-- (não é FK, é só um espelho do user_id)

-- Apenas renomeamos qualquer coluna gerada errada (se foi criada com erro)
-- Isso evita erros com insertable = false/updatable = false no JPA
-- Se já estiver certo, esse bloco será ignorado
ALTER TABLE request_audit_log
    ALTER COLUMN user_id_ref DROP DEFAULT;
