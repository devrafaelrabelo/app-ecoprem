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
  account_locked_at,
  account_deletion_requested,
  account_deletion_request_date,
  origin,
  interface_theme,
  timezone,
  notifications_enabled,
  login_attempts,
  last_password_change_ip,
  terms_accepted_at,
  privacy_policy_version,
  avatar,
  created_at,
  updated_at,
  first_login,
  preferred_language,
  invitation_status,
  account_suspended_reason,
  last_known_location,
  password_compromised,
  forced_logout_at,
  cookie_consent_status,
  manager_id,
  two_factor_secret,
  two_factor_enabled,
--  requested_by_id,
--  created_by_id,
--  position_id,
  status_id
) VALUES (
  '54b445e2-2d95-471b-9dec-a6a2c1f9808e',        -- id
  'Usuario',                                     -- first_name
  'Comum',                                       -- last_name
  'Usuario Comum',                               -- full_name
  NULL,                                          -- social_name
  'usuariocomum',                                -- username
  'usuario@empresa.com',                         -- email
  '987.654.321-00',                              -- cpf
  '1995-05-20',                                  -- birth_date
  true,                                          -- email_verified
  '$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS', -- senha: Admin@123
  NOW(),                                         -- password_last_updated
  false,                                         -- account_locked
  NULL,                                          -- account_locked_at
  false,                                         -- account_deletion_requested
  NULL,                                          -- account_deletion_request_date
  'local',                                       -- origin
  'light',                                       -- interface_theme
  'America/Sao_Paulo',                           -- timezone
  true,                                          -- notifications_enabled
  0,                                             -- login_attempts
  '192.168.0.101',                               -- last_password_change_ip
  NOW(),                                         -- terms_accepted_at
  '1.0',                                         -- privacy_policy_version
  NULL,                                          -- avatar
  NOW(),                                         -- created_at
  NOW(),                                         -- updated_at
  true,                                          -- first_login
  'pt-BR',                                       -- preferred_language
  'INVITED',                                     -- invitation_status
  NULL,                                          -- account_suspended_reason
  'Sete Lagoas - MG',                            -- last_known_location
  false,                                         -- password_compromised
  NULL,                                          -- forced_logout_at
  'ACCEPTED',                                    -- cookie_consent_status
  '9f8e8d9f-aaaa-aaaa-bbbb-cccc00001111',        -- manager_id (pode ser o admin)
  NULL,                                          -- two_factor_secret
  false,                                         -- two_factor_enabled
--  'aaaa1111-aaaa-aaaa-aaaa-aaaaaaaa1111',        -- requested_by_id
--  'bbbb2222-bbbb-bbbb-bbbb-bbbbbbbb2222',        -- created_by_id
--  '22222222-2222-2222-2222-222222222222' ,        -- position_id (ex: posição comum)
  '29d2d8e3-6165-4e80-a480-6ab4f6d7acd1'        -- status_id (ACTIVE)

);
