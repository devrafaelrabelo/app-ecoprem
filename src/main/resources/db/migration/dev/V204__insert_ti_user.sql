-- Usuário do departamento de TI
SET search_path TO security;

INSERT INTO users (
  id, first_name, last_name, full_name, social_name, username, email,
  cpf, birth_date, email_verified, password, password_last_updated,
  account_locked, account_locked_at, account_deletion_requested, account_deletion_request_date,
  origin, interface_theme, timezone, notifications_enabled, login_attempts,
  last_password_change_ip, terms_accepted_at, privacy_policy_version, avatar,
  created_at, updated_at, first_login, preferred_language, invitation_status,
  account_suspended_reason, last_known_location, password_compromised, forced_logout_at,
  cookie_consent_status, manager_id, two_factor_secret, two_factor_enabled,
  status_id
) VALUES (
  'a1000000-0000-0000-0000-000000000001', 'Ti', 'User', 'Ti User', NULL,
  'tiuser', 'ti@empresa.com', '100.100.100-00', '1991-01-01', true,
  '$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS', NOW(),
  false, NULL, false, NULL, 'local', 'light', 'America/Sao_Paulo', true, 0,
  '192.168.0.20', NOW(), '1.0', NULL,
  NOW(), NOW(), true, 'pt-BR', 'INVITED',
  NULL, NULL, false, NULL, 'ACCEPTED',
  NULL, NULL, false,
  '29d2d8e3-6165-4e80-a480-6ab4f6d7acd1'
);

-- Vincula ao departamento de TI
SET search_path TO "user";

INSERT INTO user_department (user_id, department_id) VALUES
  ('a1000000-0000-0000-0000-000000000001', '16c51bc2-2883-4077-9fdf-d625f4fcaca5');
