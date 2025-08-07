SET search_path TO security;

INSERT INTO permission (id, name, description) VALUES
  ('2ff93e48-ecb3-4db9-93ed-19873945dc0d', 'user:create', 'Permissão para criar usuários'),
  ('ce0b5b94-5aa7-42d0-9b69-6c8a3f4ae12e', 'user:update', 'Permissão para editar usuários'),
  ('e7df3f20-e9fa-4644-bd57-896cd331e1e9', 'user:delete', 'Permissão para remover usuários'),
  ('665a4be5-4744-42fc-b60f-32503ba15157', 'user:read', 'Permissão para visualizar usuários'),

  ('bd75a823-36e0-4a7d-95a1-c75655d5129b', 'activesession:read', 'Visualizar ActiveSession'),
  ('a8fd8ae4-7904-428b-b0ed-dfe5b6c93a0a', 'activesession:revoke', 'Deletar ActiveSession'),

  ('814d71b5-1a18-43fa-9a71-aaafbbdf56da', 'requestuser:delete', 'Deletar UserRequest'),
  ('bd2b2266-f8d0-464b-b9da-1fc585595179', 'requestuser:read', 'Criar UserRequest'),
  ('1429b568-e5c9-4f9f-acb8-33cfc1ddb2dc', 'requestuser:create', 'Criar UserRequest'),

  ('0deee6ed-ed3d-4c2b-87fb-719be711ee2f', 'permission:assign', 'Atribuir Permissoes'),
  ('390a32b5-c1aa-4b1d-ba4f-5a309e68e612', 'permission:revoke', 'Revogar Permissoes'),
  ('b78f741d-5d5e-4e0c-83b4-3e93a21c127f', 'permission:create', 'Criar Permissão'),
  ('a16f250f-441a-4638-b738-8756d42049ac', 'permission:read',   'Visualizar Permissão'),
  ('2c7f3a59-5096-48b7-9439-0bda94bfa1cf', 'permission:update', 'Atualizar Permissão'),
  ('e7d4a3f0-3c79-4e0c-8cb6-bf58fcd032a9', 'permission:delete', 'Excluir Permissão'),

  ('26df747b-0c3d-4cb5-8dd1-0a1d8901f1c5', 'role:assign', 'Atribuir Roles'),
  ('7e274053-6d3e-4c23-8e3f-28dbb1818cb2', 'role:revoke', 'Revogar Roles'),
  ('42a7e905-0f6c-4c91-bf29-f960fa865ce3', 'role:create', 'Criar Role'),
  ('1f7a58e3-953c-4d72-9122-e3b9da894eee', 'role:read',   'Visualizar Role'),
  ('fa1f4d6b-5ff7-4f7b-b948-2d7724a002e0', 'role:update', 'Atualizar Role'),
  ('8f2b5f9a-720f-4d1d-9aa1-9321b68ccf27', 'role:delete', 'Excluir Role'),

  ('9e0b69c6-38cf-4323-b7df-3f4f1d49a861', 'audit:read', 'Visualizar log'),

  ('fae595a4-bf3b-443e-9c69-64d1fcf2c89b', 'resource:read', 'Visualizar recursos'),
  ('f44a41b1-97f5-4d8e-a6e1-56abbd53e548', 'resource:create', 'Criar novos recursos'),
  ('2a878408-d131-48a9-a9f5-e355947f93a3', 'resource:update', 'Atualizar informações de recursos'),
  ('90ec2966-7b8e-48c7-b804-8b7f351fd3ae', 'resource:delete', 'Remover recursos existentes'),

  ('b1be79e1-d032-460c-8e5e-e20ed383fa8d', 'resourcetype:read', 'Visualizar tipos de recurso'),
  ('e81df0a4-cf83-4819-88ed-962cfa91fef6', 'resourcetype:create', 'Criar novos tipos de recurso'),
  ('f69c6a9a-7a7e-4ea3-bff0-32a005cb2ae6', 'resourcetype:update', 'Atualizar tipos de recurso'),
  ('8c2ec071-eacd-4a1a-87c0-5b25a2e2b8bb', 'resourcetype:delete', 'Remover tipos de recurso'),

  ('0d61b3de-bd56-4c18-91d5-bcf9056110c7', 'resourcestatus:read', 'Visualizar status de recurso'),
  ('baf75c11-65b5-4b6d-9f23-f0e16d7a6da4', 'resourcestatus:create', 'Criar novos status de recurso'),
  ('a68faee6-9c88-4093-9d86-3e8a7ff06712', 'resourcestatus:update', 'Atualizar status de recurso'),
  ('f11c841a-7434-4a4b-8451-5bba49aaf8c5', 'resourcestatus:delete', 'Remover status de recurso'),

  ('245f0de2-f39c-44b6-9e42-262022380ce8', 'corporate-phone:create', 'Criar telefone corporativo'),
  ('496f75f4-3f5c-4f92-b60a-b3d87f0dfe6b', 'corporate-phone:update', 'Atualizar telefone corporativo'),
  ('b5982c5e-7e70-4a27-9d38-cde14acdf818', 'corporate-phone:delete', 'Excluir telefone corporativo'),
  ('fcf2219a-b46e-464c-bac6-3a80306ce2f7', 'corporate-phone:read', 'Visualizar telefones corporativos'),

  ('7e847e0b-36ef-4a6d-818f-53e7e01e34f1', 'internal-extension:create', 'Criar ramal interno'),
  ('d7e92b47-2591-4e38-b3a5-1b0939d5a35f', 'internal-extension:update', 'Atualizar ramal interno'),
  ('ae7473c5-b49e-4df3-9bfa-e2dfc7e98b2b', 'internal-extension:delete', 'Excluir ramal interno'),
  ('a5e89b17-5a0d-4987-9c1b-5ef14c3e4bd6', 'internal-extension:read', 'Visualizar ramais internos'),

  ('d37e271d-a330-4040-8ab8-9c282b2b59af', 'teste:view', 'Criada no teste'),

  ('a101d511-9dc2-4ff7-89a2-5aafcc8f17f1', 'computer:read', 'Visualizar inventário, status, histórico, alertas'),
  ('c2f1ad34-d82b-4a55-8f32-35129c2a0d21', 'computer:create', 'Cadastrar novas máquinas'),
  ('1f341bd9-264c-4c9c-b7f2-4c5b7f13b617', 'computer:update', 'Atualizar dados e alocar/desalocar'),
  ('9b73a26e-b39c-456f-8e25-d48b960e69fc', 'computer:security', 'Acessar informações de risco, segurança e incidentes'),
  ('72dd1916-8911-40ad-9967-4df83b3142f1', 'computer:report', 'Acessar relatórios e exportações'),
  ('e24cf6a1-92c2-4a44-b177-4b6f1a7ff377', 'computer:audit', 'Acessar logs e rastreamento de acessos'),

  ('89d82b44-8d3b-41ef-9cb8-1e7e98cc99f1', 'position:read', 'Permissão para visualizar cargos'),
  ('7a7e3db2-cb14-408b-998f-c10b32a40c92', 'position:create', 'Permissão para criar cargos'),
  ('a6453a69-0c2e-4b58-a36b-e5c56a9d9a4c', 'position:update', 'Permissão para editar cargos'),
  ('cb9991aa-41be-4526-9a44-e6b4d926e981', 'position:delete', 'Permissão para remover cargos'),
  ('b2cc1d8d-b9ae-4ae5-b581-061ce92bdb93', 'position:assign', 'Permissão para atribuir cargos a usuários'),

  ('74ab62c2-99f7-460f-b9a2-1cdbf0461d00', 'company:create', 'Permissão para criar empresas'),
  ('372d94de-f45c-4868-a846-6c2ce35f3d33', 'company:update', 'Permissão para editar empresas'),
  ('83c9b3c0-7c9f-43f5-93a6-0a94a0f08455', 'company:delete', 'Permissão para remover empresas'),
  ('fd391a67-fd6f-47c4-90f7-f30e58f233fa', 'company:read', 'Permissão para visualizar empresas'),

  ('2781c8ab-f1a0-4f3a-9ad8-c16a3c120d40', 'department:read', 'Permissão para visualizar departamentos'),
  ('39718e22-cf65-4bb8-a2e2-287453a2fc5f', 'department:create', 'Permissão para criar departamentos'),
  ('4ad5a7b7-e45f-41ec-8e70-4574d3b33ad1', 'department:update', 'Permissão para editar departamentos'),
  ('53b999b5-e306-4f7c-8c5d-7a4ef927eaf0', 'department:delete', 'Permissão para remover departamentos'),
  ('61338791-6f2c-4be6-b4a3-f59f23a369cb', 'department:assignManager', 'Permissão para atribuir gerente a departamentos'),

  ('f8d2a918-7e1f-4dc2-b01c-c62ea9e4c101', 'function:read', 'Permissão para visualizar funções'),
  ('a4cd34e3-b0d3-4f1d-86f4-4b34e1252031', 'function:create', 'Permissão para criar funções'),
  ('4b9d80c9-0d8a-4c5d-91f7-d47f85c6c028', 'function:update', 'Permissão para editar funções'),
  ('8b792fbd-6a0a-4300-82ae-5decd6ab2bbf', 'function:delete', 'Permissão para remover funções'),
  ('d78e49cb-2b2c-42f9-bb91-29a4ae09c85a', 'function:assign', 'Permissão para atribuir funções a usuários');

