-- VXX__alter_users_add_position_id.sql
ALTER TABLE users
ADD COLUMN IF NOT EXISTS position_id UUID,
ADD CONSTRAINT fk_users_position FOREIGN KEY (position_id) REFERENCES position(id);
