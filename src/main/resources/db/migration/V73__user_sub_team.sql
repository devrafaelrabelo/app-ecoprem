
-- Índices para melhorar performance de consultas
CREATE INDEX idx_user_sub_team_user_id ON user_sub_team(user_id);
CREATE INDEX idx_user_sub_team_sub_team_id ON user_sub_team(sub_team_id);