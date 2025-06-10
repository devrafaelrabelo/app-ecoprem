-- Adiciona coluna para nome de usuário autenticado
ALTER TABLE request_audit_log
ADD COLUMN IF NOT EXISTS username VARCHAR(150);

-- Adiciona coluna para duração da requisição em milissegundos
ALTER TABLE request_audit_log
ADD COLUMN IF NOT EXISTS duration_ms INT;
