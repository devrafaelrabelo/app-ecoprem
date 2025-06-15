CREATE TABLE allocation_history (
    id UUID PRIMARY KEY,
    resource_type VARCHAR(50) NOT NULL,
    resource_id UUID NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    source_company_id UUID REFERENCES company(id) ON DELETE SET NULL,
    notes TEXT,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP,
    registered_by UUID
);