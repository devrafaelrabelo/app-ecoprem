SET search_path TO security;

INSERT INTO user_status (id, name, description, is_active) VALUES
  ('29d2d8e3-6165-4e80-a480-6ab4f6d7acd1', 'ACTIVE', 'Usuário ativo', true),
  ('f229cdaa-b34e-4f89-b335-ee91cc4506e2', 'SUSPENDED', 'Usuário suspenso temporariamente', false),
  ('6f9b17e2-5220-47ff-960f-df7567a307dc', 'PENDING', 'Conta pendente de ativação', true),
  ('41cef5d3-ad9b-400c-a0e5-677c9e1433e1', 'DISABLED', 'Conta desativada permanentemente', false),
  ('6af4e4a9-6c10-4159-abc3-2c7aa25d9843', 'LOCKED', 'Conta bloqueada por segurança', false);