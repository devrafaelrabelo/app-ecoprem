-- V12__revoked_token_column_token_text.sql

ALTER TABLE revoked_token
    ALTER COLUMN token TYPE TEXT;