CREATE SCHEMA IF NOT EXISTS resource;
SET search_path TO resource;

CREATE TABLE IF NOT EXISTS allocation_history (
    id UUID PRIMARY KEY,

    resource_type VARCHAR(50),
    resource_id UUID NOT NULL,

    user_id UUID REFERENCES security.users(id) ON DELETE SET NULL,
    source_company_id UUID REFERENCES common.company(id) ON DELETE SET NULL,
    company_id UUID REFERENCES common.company(id),

    notes TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    created_at TIMESTAMP,

    registered_by UUID REFERENCES security.users(id)
);