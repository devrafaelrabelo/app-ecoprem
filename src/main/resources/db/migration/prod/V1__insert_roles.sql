SET search_path TO security;

INSERT INTO role (id, name, description, is_system_role) VALUES
  ('141876fd-164b-417a-ac7e-69089703351e', 'ROLE_ADMIN', 'Acesso administrativo completo', true),
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', 'ROLE_USER_MANAGER', 'Gerenciar usuários', false),
  ('75b2b6c2-862d-4d8d-91c8-14929f0b419d', 'ROLE_RESOURCE_MANAGER', 'Gerenciar recursos', false),
  ('a6c78d53-ae9f-4d97-ba0f-7a594ceba0b0', 'ROLE_RESOURCE_TYPE_MANAGER', 'Gerenciar tipos de recursos', false),
  ('44e2d257-45ad-442a-bc63-ebb6fc9753a9', 'ROLE_RESOURCE_STATUS_MANAGER', 'Gerenciar status de recursos', false),
  ('ce9aaef9-8a0b-41d5-91e4-fb29669c5479', 'ROLE_PHONE_MANAGER', 'Gerenciar telefones corporativos', false),
  ('2a0fc18c-6ef0-4cb1-a625-3c5b83bb4029', 'ROLE_EXTENSION_MANAGER', 'Gerenciar ramais internos', false),
  ('bf2ed4f4-91b0-4a5b-8e52-b730d2c6dc84', 'ROLE_PERMISSION_ADMIN', 'Gerenciar permissões', true),
  ('df1801d3-361c-4376-b315-0cfe93a97890', 'ROLE_ROLE_ADMIN', 'Gerenciar roles de usuários', false);
