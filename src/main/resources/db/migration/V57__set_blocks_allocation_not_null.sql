-- Define valor padrão antes de aplicar a restrição
UPDATE resource_status
SET blocks_allocation = FALSE
WHERE blocks_allocation IS NULL;

-- Torna a coluna blocks_allocation obrigatória (NOT NULL)
ALTER TABLE resource_status
ALTER COLUMN blocks_allocation SET NOT NULL;
