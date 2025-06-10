INSERT INTO role (id, name, description, is_system_role)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'ADMIN', 'Administrator with full access', TRUE),
  ('22222222-2222-2222-2222-222222222222', 'USER', 'Standard user', FALSE)
ON CONFLICT (id) DO NOTHING;