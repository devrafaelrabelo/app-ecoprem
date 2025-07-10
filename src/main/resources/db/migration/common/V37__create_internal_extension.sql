SET search_path TO security;

CREATE TABLE IF NOT EXISTS internal_extension (
    id UUID PRIMARY KEY,
    extension VARCHAR(10) NOT NULL,
    application VARCHAR(50),
    current_user_id UUID REFERENCES security.users(id) ON DELETE SET NULL,
    company_id UUID REFERENCES security.company(id) ON DELETE CASCADE,
    last_updated TIMESTAMP
);
