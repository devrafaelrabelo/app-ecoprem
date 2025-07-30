-- Usuário do departamento de RH
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
  'a1000000-0000-0000-0000-000000000002', 'Rh', 'User', 'Rh User', NULL,
  'rhuser', 'rh@empresa.com', '100.100.100-01', '1992-02-02', true,
  '$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS', NOW(),
  false, NULL, false, NULL, 'local', 'light', 'America/Sao_Paulo', true, 0,
  '192.168.0.21', NOW(), '1.0', NULL,
  NOW(), NOW(), true, 'pt-BR', 'INVITED',
  NULL, NULL, false, NULL, 'ACCEPTED',
  NULL, NULL, false,
  '29d2d8e3-6165-4e80-a480-6ab4f6d7acd1'
);

-- Vincula ao departamento de RH
SET search_path TO "user";

INSERT INTO user_department (user_id, department_id) VALUES
  ('a1000000-0000-0000-0000-000000000002', '652b8f41-af54-4dd7-b3af-ee7b4c4d0257');
