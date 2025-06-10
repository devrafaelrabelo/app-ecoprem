-- Insere as relações entre usuários e papéis
-- Admin
INSERT INTO user_role (user_id, role_id) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '11111111-1111-1111-1111-111111111111')
ON CONFLICT DO NOTHING;

-- Usuários padrão
INSERT INTO user_role (user_id, role_id) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222')
ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_id) VALUES
('b5c40444-d849-4b56-a3d1-f58d6b0d0caa', '22222222-2222-2222-2222-222222222222')
ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_id) VALUES
('ffffffff-ffff-ffff-ffff-ffffffffffff', '22222222-2222-2222-2222-222222222222')
ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_id) VALUES
('b4499d4c-b224-46f2-a8e6-c64cb21409c0', '22222222-2222-2222-2222-222222222222')
ON CONFLICT DO NOTHING;