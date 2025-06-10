-- Insere usuário administrador
INSERT INTO users (
    id, first_name, last_name, social_name, username, email,
    email_verified, password, password_last_updated, account_locked,
    account_deletion_requested, origin, created_at, updated_at,
    role_id, access_level_id, status_id, department_id, group_id
) VALUES (
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    'Admin', 'Principal', 'Admin Master',
    'admin', 'admin@example.com', TRUE,
    '$2a$12$Fj3/ltcWXfBKAL1SBwps/efwLpfbpGLB1pj5g469PxD9Og7.jlit2',
    NOW(), FALSE, FALSE,
    'system', NOW(), NOW(),
    '11111111-1111-1111-1111-111111111111',
    '33333333-3333-3333-3333-333333333333',
    '66666666-6666-6666-6666-666666666666',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'cccccccc-cccc-cccc-cccc-cccccccccccc'
);

-- Insere usuário padrão
INSERT INTO users (
    id, first_name, last_name, social_name, username, email,
    email_verified, password, password_last_updated, account_locked,
    account_deletion_requested, origin, created_at, updated_at,
    role_id, access_level_id, status_id, department_id, group_id
) VALUES (
    'dddddddd-dddd-dddd-dddd-dddddddddddd',
    'Usuário', 'Padrão', 'Usuário Padrão',
    'user', 'user@example.com', TRUE,
    '$2a$12$Fj3/ltcWXfBKAL1SBwps/efwLpfbpGLB1pj5g469PxD9Og7.jlit2',
    NOW(), FALSE, FALSE,
    'system', NOW(), NOW(),
    '22222222-2222-2222-2222-222222222222',  -- role_id (USER)
    '33333333-3333-3333-3333-333333333333',  -- access_level_id (Standard)
    '66666666-6666-6666-6666-666666666666',  -- status_id (active)
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  -- department_id (Technology)
    'cccccccc-cccc-cccc-cccc-cccccccccccc'   -- group_id (Group A)
);
