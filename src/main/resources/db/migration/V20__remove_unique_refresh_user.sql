-- 🔧 Remove UNIQUE constraint antiga em user_id
ALTER TABLE refresh_token
DROP CONSTRAINT IF EXISTS refresh_token_user_id_key;

-- ✅ Cria nova constraint composta para evitar duplicação por sessão
ALTER TABLE refresh_token
ADD CONSTRAINT uq_refresh_user_session UNIQUE(user_id, session_id);

-- (Opcional) Índices para performance
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_session_id ON refresh_token(session_id);