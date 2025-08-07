--SET search_path TO security;
--
--INSERT INTO users (
--  id,
--  first_name,
--  last_name,
--  full_name,
--  social_name,
--  username,
--  email,
--  cpf,
--  birth_date,
--  email_verified,
--  password,
--  password_last_updated,
--  account_locked,
--  account_locked_at,
--  account_deletion_requested,
--  account_deletion_request_date,
--  origin,
--  interface_theme,
--  timezone,
--  notifications_enabled,
--  login_attempts,
--  last_password_change_ip,
--  terms_accepted_at,
--  privacy_policy_version,
--  avatar,
--  created_at,
--  updated_at,
--  first_login,
--  preferred_language,
--  invitation_status,
--  account_suspended_reason,
--  last_known_location,
--  password_compromised,
--  forced_logout_at,
--  cookie_consent_status,
--  manager_id,
--  two_factor_secret,
--  two_factor_enabled,
----  requested_by_id,
----  created_by_id,
----  position_id,
--  status_id
--) VALUES (
--  '8d9e8d9f-92ab-a5b7-ff6c-889900112233',        -- id
--  'Admin',                                       -- first_name
--  'Teste',                                       -- last_name
--  'Admin Teste',                                 -- full_name
--  'Administrador',                               -- social_name
--  'adminteste',                                  -- username
--  'admin@empresa.com',                           -- email
--  '123.456.789-00',                              -- cpf
--  '1990-01-01',                                  -- birth_date
--  true,                                          -- email_verified
--  '$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS',  -- password (bcrypt de Admin@123)
--  NOW(),                                         -- password_last_updated
--  false,                                         -- account_locked
--  NULL,                                          -- account_locked_at
--  false,                                         -- account_deletion_requested
--  NULL,                                          -- account_deletion_request_date
--  'local',                                       -- origin
--  'light',                                       -- interface_theme
--  'America/Sao_Paulo',                           -- timezone
--  true,                                          -- notifications_enabled
--  0,                                             -- login_attempts
--  '192.168.0.10',                                -- last_password_change_ip
--  NOW(),                                         -- terms_accepted_at
--  '1.0',                                         -- privacy_policy_version
--  'https://cdn.app.com/avatar/admin.png',        -- avatar
--  NOW(),                                         -- created_at
--  NOW(),                                         -- updated_at
--  true,                                          -- first_login
--  'pt-BR',                                       -- preferred_language
--  'INVITED',                                     -- invitation_status
--  NULL,                                          -- account_suspended_reason
--  'Sete Lagoas - MG',                            -- last_known_location
--  false,                                         -- password_compromised
--  NULL,                                          -- forced_logout_at
--  'ACCEPTED',                                    -- cookie_consent_status
--  '9f8e8d9f-aaaa-aaaa-bbbb-cccc00001111',        -- manager_id
--  NULL,                                          -- two_factor_secret
--  false,                                         -- two_factor_enabled
----  'aaaa1111-aaaa-aaaa-aaaa-aaaaaaaa1111',        -- requested_by_id
----  'bbbb2222-bbbb-bbbb-bbbb-bbbbbbbb2222',        -- created_by_id
----  '22222222-2222-2222-2222-222222222222',         -- position_id
--  '29d2d8e3-6165-4e80-a480-6ab4f6d7acd1'        -- status_id (ACTIVE)
--
--);


-- Cria o usuário administrador comum
SET search_path TO security;

INSERT INTO users (
  id,
  first_name,
  last_name,
  full_name,
  social_name,
  username,
  email,
  cpf,
  birth_date,
  email_verified,
  password,
  password_last_updated,
  account_locked,
  account_locked_at, account_deletion_requested, account_deletion_request_date,
  origin, interface_theme, timezone, notifications_enabled, login_attempts,
  last_password_change_ip, terms_accepted_at, privacy_policy_version, avatar,
  created_at, updated_at, first_login, preferred_language, invitation_status,
  account_suspended_reason, last_known_location, password_compromised, forced_logout_at,
  cookie_consent_status, manager_id, two_factor_secret, two_factor_enabled,
  status_id
) VALUES (
  '8d9e8d9f-92ab-a5b7-ff6c-889900112233','Admin', 'Teste', 'Admin Teste', 'Administrador',
    'adminteste', 'admin@empresa.com', '123.456.789-00', '1990-01-01', true,
    E'$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS', NOW(),
    false, NULL, false, NULL, 'local', 'light', 'America/Sao_Paulo', true, 0,
    '192.168.0.10', NOW(), '1.0', 'https://cdn.app.com/avatar/admin.png',
    NOW(), NOW(), true, 'pt-BR', 'INVITED',
    NULL, 'Sete Lagoas - MG', false, NULL, 'ACCEPTED',
    '9f8e8d9f-aaaa-aaaa-bbbb-cccc00001111', NULL, false,
    '29d2d8e3-6165-4e80-a480-6ab4f6d7acd1'
  );

SET search_path TO security;
-- Relacionamento user_role (usuário admin ↔ role admin)
INSERT INTO user_role (user_id, role_id) VALUES (
'8d9e8d9f-92ab-a5b7-ff6c-889900112233',  -- ID do usuário admin
'141876fd-164b-417a-ac7e-69089703351e'   -- ID da role ADMIN
  );




