-- Adiciona coluna revoked_at na tabela revoked_token
ALTER TABLE revoked_token
ADD COLUMN revoked_at TIMESTAMP NOT NULL DEFAULT now();