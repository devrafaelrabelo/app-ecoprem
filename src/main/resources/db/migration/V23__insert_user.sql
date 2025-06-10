-- Insere segundo usuário padrão
INSERT INTO users (
    id, first_name, last_name, social_name, username, email,
    email_verified, password, password_last_updated, account_locked,
    account_deletion_requested, origin, created_at, updated_at,
    role_id, access_level_id, status_id, department_id, group_id
) VALUES (
   'ffffffff-ffff-ffff-ffff-ffffffffffff',
   'Usuário1', 'Padrão', 'Usuário1 Padrão',
   'user1', 'user1@example.com', TRUE,
   '$2a$12$Fj3/ltcWXfBKAL1SBwps/efwLpfbpGLB1pj5g469PxD9Og7.jlit2',
    NOW(), FALSE, FALSE,
    'system', NOW(), NOW(),
    '22222222-2222-2222-2222-222222222222',  -- role_id (USER)
    '33333333-3333-3333-3333-333333333333',  -- access_level_id (Standard)
    '77777777-7777-7777-7777-777777777777',  -- status_id (active)
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  -- department_id (Technology)
    'cccccccc-cccc-cccc-cccc-cccccccccccc'   -- group_id (Group A)
);

-- Insere segundo usuário padrão
INSERT INTO users (
    id, first_name, last_name, social_name, username, email,
    email_verified, password, password_last_updated, account_locked,
    account_deletion_requested, origin, created_at, updated_at,
    role_id, access_level_id, status_id, department_id, group_id
) VALUES (
    'b5c40444-d849-4b56-a3d1-f58d6b0d0caa',
    'Usuário2', 'Padrão', 'Usuário Padrão',
    'user2', 'user2@example.com', TRUE,
    '$2a$12$Fj3/ltcWXfBKAL1SBwps/efwLpfbpGLB1pj5g469PxD9Og7.jlit2',
    NOW(), FALSE, FALSE,
    'system', NOW(), NOW(),
    '22222222-2222-2222-2222-222222222222',  -- role_id (USER)
    '33333333-3333-3333-3333-333333333333',  -- access_level_id (Standard)
    '88888888-8888-8888-8888-888888888888',  -- status_id (active)
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  -- department_id (Technology)
    'cccccccc-cccc-cccc-cccc-cccccccccccc'   -- group_id (Group A)
);