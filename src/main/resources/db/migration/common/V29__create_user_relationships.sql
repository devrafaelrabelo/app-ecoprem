SET search_path TO security;

-- Relacionamento com position (ManyToOne)
ALTER TABLE users
ADD COLUMN IF NOT EXISTS position_id UUID;

ALTER TABLE users
ADD CONSTRAINT fk_user_position FOREIGN KEY (position_id) REFERENCES common.position(id);
