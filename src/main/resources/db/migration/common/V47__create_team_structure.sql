SET search_path TO security;

-- ================================
-- Tabela: team
-- ================================
CREATE TABLE IF NOT EXISTS team (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  location VARCHAR(150),
  supervisor_id UUID REFERENCES users(id),
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ================================
-- Tabela: sub_team
-- ================================
CREATE TABLE IF NOT EXISTS sub_team (
  id UUID PRIMARY KEY,
  team_id UUID NOT NULL REFERENCES team(id),
  name VARCHAR(100) NOT NULL,
  description TEXT,
  manager_id UUID REFERENCES users(id),
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ================================
-- Tabela: user_sub_team
-- ================================
CREATE TABLE IF NOT EXISTS user_sub_team (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  sub_team_id UUID NOT NULL REFERENCES sub_team(id),
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ================================
-- Função de trigger para updated_at
-- ================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ================================
-- Triggers de atualização de updated_at
-- ================================
CREATE TRIGGER trg_update_team_updated_at
BEFORE UPDATE ON team
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_update_sub_team_updated_at
BEFORE UPDATE ON sub_team
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ===========================================
-- Índices para melhorar performance de queries
-- ===========================================
CREATE INDEX IF NOT EXISTS idx_user_sub_team_user_id ON security.user_sub_team(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sub_team_sub_team_id ON security.user_sub_team(sub_team_id);
