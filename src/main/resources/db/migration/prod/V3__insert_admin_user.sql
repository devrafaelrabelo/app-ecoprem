SET search_path TO security;

-- Usuário ADMIN (produção)
INSERT INTO users (
  id, first_name, last_name, full_name, social_name, username, email,
  email_verified, password, password_last_updated, account_locked,
  origin, interface_theme, timezone, notifications_enabled,
  login_attempts, first_login, preferred_language,
  cpf, birth_date,
  status_id, created_at, updated_at
) VALUES (
  '9d650cd5-9d22-4f3a-af7d-83d199e921d0', 'Admin', 'Sistema', 'Admin Sistema', NULL, 'adminappgestaoti',
  'adminappgestaoti@bemprotege.com.br', true,
  '$2a$10$7QniyDg1Bxw9zvLLEnkmfeP/sY5l7E/Fy9AyXz9MFsVrRNLcBp6Ga', NOW(), false,
  'prod', 'dark', 'America/Sao_Paulo', true,
  0, false, 'pt-BR',
  '111.222.333-44', '1985-01-01',
  '29d2d8e3-6165-4e80-a480-6ab4f6d7acd1', NOW(), NOW()
);
