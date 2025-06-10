-- V19__refactor_refresh_token_session.sql

-- 1. Remove constraint UNIQUE se houver (de @OneToOne)
ALTER TABLE refresh_token DROP CONSTRAINT IF EXISTS uk_refresh_token_user;

-- 2. Adiciona coluna session_id
ALTER TABLE refresh_token
ADD COLUMN session_id VARCHAR(255) NOT NULL;

-- 3. Adiciona índice único por usuário + sessão
ALTER TABLE refresh_token
ADD CONSTRAINT uk_refresh_token_user_session UNIQUE (user_id, session_id);

-- 4. Ajusta índice para token (caso não exista)
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_token_token
    ON refresh_token (token);

-- 5. Opcional: se quiser migrar tokens existentes com sessionId genérico
UPDATE refresh_token
SET session_id = gen_random_uuid()::text
WHERE session_id IS NULL;
