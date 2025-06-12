-- Remove UNIQUE antigo, se existir
ALTER TABLE refresh_token DROP CONSTRAINT IF EXISTS uk_refresh_token_user;

-- Adiciona session_id
ALTER TABLE refresh_token
ADD COLUMN IF NOT EXISTS session_id VARCHAR(255) NOT NULL;

-- Índice único por user + session
ALTER TABLE refresh_token
ADD CONSTRAINT uq_refresh_user_session UNIQUE(user_id, session_id);

-- Índice para token (caso não exista)
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_token_token
ON refresh_token (token);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_session_id ON refresh_token(session_id);
