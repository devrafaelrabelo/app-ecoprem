SET search_path TO resource;

INSERT INTO resource_status (id, code, name, description, blocks_allocation) VALUES
  ('0afd6efd-cebf-4609-acca-5ec37d3b216f', 'DISPONIVEL', 'Disponível', 'Recurso pronto para ser alocado', FALSE),
  ('7a1df507-73b3-46f8-84a8-62b1604a281c', 'ALOCADO', 'Alocado', 'Recurso em uso por um colaborador', TRUE),
  ('8f8fb668-9f9f-405e-a919-773d6f4250c1', 'MANUTENCAO', 'Em manutenção', 'Recurso fora de uso por manutenção', TRUE),
  ('8fea533d-b578-4e88-a487-cecc14c8b185', 'DESCARTADO', 'Descartado', 'Recurso aposentado, inutilizado ou doado', TRUE),
  ('14b46508-dd4b-4e51-b70c-c6842a7ef785', 'PERDIDO', 'Perdido', 'Recurso extraviado ou não localizado', TRUE),
  ('fc454def-97e2-4e1e-9699-07eafa06754d', 'BLOQUEADO', 'Bloqueado', 'Indisponível temporariamente ou por processo interno', TRUE);
