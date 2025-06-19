-- Atualiza registros nulos antes de aplicar restrição
UPDATE resource_type
SET active = TRUE
WHERE active IS NULL;

-- Aplica restrição NOT NULL
ALTER TABLE resource_type
ALTER COLUMN active SET NOT NULL;
