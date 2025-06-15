CREATE TABLE internal_extension (
    id UUID PRIMARY KEY,
    extension VARCHAR(10) NOT NULL,
    application VARCHAR(50),
    current_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    company_id UUID NOT NULL REFERENCES company(id) ON DELETE CASCADE,
    last_updated TIMESTAMP
);