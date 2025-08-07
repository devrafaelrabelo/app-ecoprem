SET search_path TO security;

INSERT INTO role_permission (role_id, permission_id) VALUES
  -- ROLE_USER_MANAGER (requestuser:*)
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', '814d71b5-1a18-43fa-9a71-aaafbbdf56da'),
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', 'bd2b2266-f8d0-464b-b9da-1fc585595179'),
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', '1429b568-e5c9-4f9f-acb8-33cfc1ddb2dc'),

  -- ROLE_USER_MANAGER
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', '2ff93e48-ecb3-4db9-93ed-19873945dc0d'),
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', 'ce0b5b94-5aa7-42d0-9b69-6c8a3f4ae12e'),
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', 'e7df3f20-e9fa-4644-bd57-896cd331e1e9'),
  ('f38b9a01-f1d5-4d66-9bb8-77a1fdf752f3', '665a4be5-4744-42fc-b60f-32503ba15157'),

  -- ROLE_RESOURCE_MANAGER (resource:*)
  ('75b2b6c2-862d-4d8d-91c8-14929f0b419d', 'f44a41b1-97f5-4d8e-a6e1-56abbd53e548'),
  ('75b2b6c2-862d-4d8d-91c8-14929f0b419d', '2a878408-d131-48a9-a9f5-e355947f93a3'),
  ('75b2b6c2-862d-4d8d-91c8-14929f0b419d', '90ec2966-7b8e-48c7-b804-8b7f351fd3ae'),
  ('75b2b6c2-862d-4d8d-91c8-14929f0b419d', 'fae595a4-bf3b-443e-9c69-64d1fcf2c89b'),

  -- ROLE_RESOURCE_TYPE_MANAGER (resourcetype:*)
  ('a6c78d53-ae9f-4d97-ba0f-7a594ceba0b0', 'b1be79e1-d032-460c-8e5e-e20ed383fa8d'),
  ('a6c78d53-ae9f-4d97-ba0f-7a594ceba0b0', 'e81df0a4-cf83-4819-88ed-962cfa91fef6'),
  ('a6c78d53-ae9f-4d97-ba0f-7a594ceba0b0', 'f69c6a9a-7a7e-4ea3-bff0-32a005cb2ae6'),
  ('a6c78d53-ae9f-4d97-ba0f-7a594ceba0b0', '8c2ec071-eacd-4a1a-87c0-5b25a2e2b8bb'),

  -- ROLE_RESOURCE_STATUS_MANAGER (resourcestatus:*)
  ('44e2d257-45ad-442a-bc63-ebb6fc9753a9', '0d61b3de-bd56-4c18-91d5-bcf9056110c7'),
  ('44e2d257-45ad-442a-bc63-ebb6fc9753a9', 'baf75c11-65b5-4b6d-9f23-f0e16d7a6da4'),
  ('44e2d257-45ad-442a-bc63-ebb6fc9753a9', 'a68faee6-9c88-4093-9d86-3e8a7ff06712'),
  ('44e2d257-45ad-442a-bc63-ebb6fc9753a9', 'f11c841a-7434-4a4b-8451-5bba49aaf8c5'),

  -- ROLE_PHONE_MANAGER (corporate-phone:*)
  ('ce9aaef9-8a0b-41d5-91e4-fb29669c5479', '245f0de2-f39c-44b6-9e42-262022380ce8'),
  ('ce9aaef9-8a0b-41d5-91e4-fb29669c5479', '496f75f4-3f5c-4f92-b60a-b3d87f0dfe6b'),
  ('ce9aaef9-8a0b-41d5-91e4-fb29669c5479', 'b5982c5e-7e70-4a27-9d38-cde14acdf818'),
  ('ce9aaef9-8a0b-41d5-91e4-fb29669c5479', 'fcf2219a-b46e-464c-bac6-3a80306ce2f7'),

  -- ROLE_EXTENSION_MANAGER (internal-extension:*)
  ('2a0fc18c-6ef0-4cb1-a625-3c5b83bb4029', '7e847e0b-36ef-4a6d-818f-53e7e01e34f1'),
  ('2a0fc18c-6ef0-4cb1-a625-3c5b83bb4029', 'd7e92b47-2591-4e38-b3a5-1b0939d5a35f'),
  ('2a0fc18c-6ef0-4cb1-a625-3c5b83bb4029', 'ae7473c5-b49e-4df3-9bfa-e2dfc7e98b2b'),
  ('2a0fc18c-6ef0-4cb1-a625-3c5b83bb4029', 'a5e89b17-5a0d-4987-9c1b-5ef14c3e4bd6'),

  -- Permissões de gerenciamento de máquinas (computer)
  ('5f1d8a6b-98c2-41f4-b624-cb113f2ef0cc', 'a101d511-9dc2-4ff7-89a2-5aafcc8f17f1'), -- computer:read
  ('5f1d8a6b-98c2-41f4-b624-cb113f2ef0cc', 'c2f1ad34-d82b-4a55-8f32-35129c2a0d21'), -- computer:create
  ('5f1d8a6b-98c2-41f4-b624-cb113f2ef0cc', '1f341bd9-264c-4c9c-b7f2-4c5b7f13b617'), -- computer:update
  ('5f1d8a6b-98c2-41f4-b624-cb113f2ef0cc', '9b73a26e-b39c-456f-8e25-d48b960e69fc'), -- computer:security
  ('5f1d8a6b-98c2-41f4-b624-cb113f2ef0cc', '72dd1916-8911-40ad-9967-4df83b3142f1'), -- computer:report
  ('5f1d8a6b-98c2-41f4-b624-cb113f2ef0cc', 'e24cf6a1-92c2-4a44-b177-4b6f1a7ff377'); -- computer:audit
