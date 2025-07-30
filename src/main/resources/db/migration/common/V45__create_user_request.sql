CREATE SCHEMA IF NOT EXISTS "user";
SET search_path TO "user";

CREATE TABLE IF NOT EXISTS user_request (
  id UUID PRIMARY KEY,

  cpf VARCHAR(14) NOT NULL UNIQUE,
  birth_date DATE NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  phone VARCHAR(50) NOT NULL,

  supervisor_id UUID NOT NULL,
  leader_id UUID NOT NULL,

  cep VARCHAR(20) NOT NULL,
  street VARCHAR(255) NOT NULL,
  number VARCHAR(20) NOT NULL,
  complement VARCHAR(100),
  city VARCHAR(100) NOT NULL,
  state VARCHAR(100) NOT NULL,
  neighborhood VARCHAR(100),

  requester_id UUID REFERENCES security.users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by_id UUID,



  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX IF NOT EXISTS idx_user_request_requester_id ON "user".user_request(requester_id);