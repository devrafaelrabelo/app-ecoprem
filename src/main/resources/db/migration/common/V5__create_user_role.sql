CREATE SCHEMA IF NOT EXISTS security;
SET search_path TO security;

CREATE TABLE IF NOT EXISTS user_role (
  user_id UUID NOT NULL REFERENCES security.users(id) ON DELETE CASCADE,
  role_id UUID NOT NULL REFERENCES security.role(id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, role_id)
);