SET search_path TO security;

INSERT INTO users (
  id,
  first_name,
  last_name,
  full_name,
  social_name,
  username,
  email,
  email_verified,
  password,
  password_last_updated,
  account_locked,
  origin,
  interface_theme,
  timezone,
  notifications_enabled,
  login_attempts,
  first_login,
  preferred_language,
  cpf,
  birth_date,
  status_id,
  created_at,
  updated_at
) VALUES (
  '8d9e8d9f-92ab-a5b7-ff6c-889900112233',
  'Admin',
  'Teste',
  'Admin Teste',
  NULL,
  'adminteste',
  'admin@empresa.com',
  true,
  '$2a$10$7QniyDg1Bxw9zvLLEnkmfeP/sY5l7E/Fy9AyXz9MFsVrRNLcBp6Ga',
  NOW(),
  false,
  'local',
  'light',
  'America/Sao_Paulo',
  true,
  0,
  false,
  'pt-BR',
  '123.456.789-00',      -- exemplo de CPF
  '1990-01-01',           -- exemplo de data de nascimento
  '29d2d8e3-6165-4e80-a480-6ab4f6d7acd1', -- status ACTIVE
  NOW(),
  NOW()
);
