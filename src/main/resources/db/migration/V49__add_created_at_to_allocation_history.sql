ALTER TABLE allocation_history
ADD COLUMN company_id UUID;

ALTER TABLE allocation_history
ADD CONSTRAINT fk_allocation_history_company
FOREIGN KEY (company_id) REFERENCES company(id);