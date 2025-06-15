CREATE TABLE corporate_phone (
    id UUID PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    carrier VARCHAR(50),
    plan_type VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    current_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    company_id UUID NOT NULL REFERENCES company(id) ON DELETE CASCADE,
    last_updated TIMESTAMP
);