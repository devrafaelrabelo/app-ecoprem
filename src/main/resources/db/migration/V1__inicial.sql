-- ==========================================
-- TABELAS AUXILIARES
-- ==========================================
CREATE TABLE role (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_system_role BOOLEAN DEFAULT FALSE
);

CREATE TABLE access_level (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    level_number INT,
    description TEXT
);

CREATE TABLE user_status (
    id UUID PRIMARY KEY,
    status VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE department (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    manager_id UUID
);

CREATE TABLE user_group (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_by UUID
);

-- ==========================================
-- TABELA PRINCIPAL: USERS (agora com full_name)
-- ==========================================
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(200), -- NOVO CAMPO
    social_name VARCHAR(150),
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    email_verified BOOLEAN DEFAULT FALSE,
    password TEXT NOT NULL,
    password_last_updated TIMESTAMP,
    account_locked BOOLEAN DEFAULT FALSE,
    account_deletion_requested BOOLEAN DEFAULT FALSE,
    account_deletion_request_date TIMESTAMP,
    origin VARCHAR(50),
    interface_theme VARCHAR(20) DEFAULT 'light',
    timezone VARCHAR(50) DEFAULT 'America/Sao_Paulo',
    notifications_enabled BOOLEAN DEFAULT TRUE,
    login_attempts INT DEFAULT 0,
    last_password_change_ip VARCHAR(50),
    terms_accepted_at TIMESTAMP,
    privacy_policy_version VARCHAR(20),
    avatar TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    first_login BOOLEAN DEFAULT TRUE,
    preferred_language VARCHAR(10) DEFAULT 'pt-BR',
    invitation_status VARCHAR(50),
    account_suspended_reason TEXT,
    last_known_location VARCHAR(150),
    password_compromised BOOLEAN DEFAULT FALSE,
    forced_logout_at TIMESTAMP,
    cookie_consent_status VARCHAR(20),
    manager_id UUID,
    role_id UUID REFERENCES role(id),
    access_level_id UUID REFERENCES access_level(id),
    status_id UUID REFERENCES user_status(id),
    department_id UUID REFERENCES department(id),
    group_id UUID REFERENCES user_group(id)
);

-- ==========================================
-- NOVAS TABELAS: HISTÓRICO E SEGURANÇA
-- ==========================================
CREATE TABLE login_history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    login_date TIMESTAMP NOT NULL,
    ip_address VARCHAR(50),
    location VARCHAR(150),
    device VARCHAR(100),
    browser VARCHAR(50),
    operating_system VARCHAR(50),
    success BOOLEAN
);

CREATE TABLE activity_log (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    activity TEXT NOT NULL,
    activity_date TIMESTAMP NOT NULL,
    ip_address VARCHAR(50),
    location VARCHAR(150)
);

CREATE TABLE backup_code (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code VARCHAR(100) NOT NULL,
    used BOOLEAN DEFAULT FALSE
);

CREATE TABLE device_token (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_name VARCHAR(100),
    token TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE active_session (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(150) NOT NULL,
    device VARCHAR(100),
    browser VARCHAR(50),
    operating_system VARCHAR(50),
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP
);

CREATE TABLE webhook (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    url TEXT NOT NULL
);

CREATE TABLE privacy_settings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    share_activity BOOLEAN DEFAULT TRUE,
    show_online_status BOOLEAN DEFAULT TRUE
);

-- ==========================================
-- DADOS INICIAIS: ROLES
-- ==========================================
INSERT INTO role (id, name, description, is_system_role) VALUES
  ('11111111-1111-1111-1111-111111111111', 'ADMIN', 'Administrator with full access', TRUE),
  ('22222222-2222-2222-2222-222222222222', 'USER', 'Standard user', FALSE);

-- ==========================================
-- DADOS INICIAIS: ACCESS LEVELS
-- ==========================================
INSERT INTO access_level (id, name, level_number, description) VALUES
  ('33333333-3333-3333-3333-333333333333', 'Standard', 1, 'Basic access'),
  ('44444444-4444-4444-4444-444444444444', 'Advanced', 2, 'Advanced access'),
  ('55555555-5555-5555-5555-555555555555', 'Premium', 3, 'Full premium access');

-- ==========================================
-- DADOS INICIAIS: USER STATUS
-- ==========================================
INSERT INTO user_status (id, status, description, is_active) VALUES
  ('66666666-6666-6666-6666-666666666666', 'active', 'Active user', TRUE),
  ('77777777-7777-7777-7777-777777777777', 'inactive', 'Inactive user', FALSE),
  ('88888888-8888-8888-8888-888888888888', 'suspended', 'Suspended account', FALSE);

-- ==========================================
-- DADOS INICIAIS: DEPARTMENTS
-- ==========================================
INSERT INTO department (id, name, description, manager_id) VALUES
  ('99999999-9999-9999-9999-999999999999', 'Sales', 'Sales department', NULL),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Technology', 'Tech/IT department', NULL),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'HR', 'Human Resources', NULL);

-- ==========================================
-- DADOS INICIAIS: USER GROUPS
-- ==========================================
INSERT INTO user_group (id, name, description, created_by) VALUES
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Group A', 'First group of users', NULL),
  ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Group B', 'Second group of users', NULL);

-- ==========================================
-- USUÁRIO ADMIN INICIAL (com full_name)
-- ==========================================
INSERT INTO users (
    id, first_name, last_name, full_name, social_name, username, email, email_verified,
    password, password_last_updated, account_locked, account_deletion_requested,
    origin, created_at, updated_at, role_id, access_level_id, status_id,
    department_id, group_id
) VALUES (
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    'Admin', 'Principal', 'Admin Principal', 'Admin Master',
    'admin', 'admin@example.com', TRUE,
    '$2a$12$Fj3/ltcWXfBKAL1SBwps/efwLpfbpGLB1pj5g469PxD9Og7.jlit2',
    NOW(), FALSE, FALSE,
    'system', NOW(), NOW(),
    '11111111-1111-1111-1111-111111111111',  -- role_id (ADMIN)
    '33333333-3333-3333-3333-333333333333',  -- access_level_id (Standard)
    '66666666-6666-6666-6666-666666666666',  -- status_id (active)
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  -- department_id (Technology)
    'cccccccc-cccc-cccc-cccc-cccccccccccc'   -- group_id (Group A)
);

-- ==========================================
-- PRIVACY SETTINGS: ADMIN
-- ==========================================
INSERT INTO privacy_settings (id, user_id, share_activity, show_online_status) VALUES
(
    'ffffffff-ffff-ffff-ffff-ffffffffffff',
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    TRUE,
    TRUE
);
