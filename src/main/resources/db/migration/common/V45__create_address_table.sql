SET search_path TO security;

CREATE TABLE IF NOT EXISTS address (
  id UUID PRIMARY KEY,
  street VARCHAR(255),
  number VARCHAR(20),
  complement VARCHAR(100),
  city VARCHAR(100),
  state VARCHAR(100),
  country VARCHAR(100),
  postal_code VARCHAR(20),
  neighborhood VARCHAR(100),

  user_id UUID REFERENCES users(id) ON DELETE CASCADE
);
