ALTER TABLE allocation_history
ADD COLUMN resource_type_id UUID;

ALTER TABLE allocation_history
ADD CONSTRAINT fk_allocation_resource_type
FOREIGN KEY (resource_type_id)
REFERENCES resource_type(id);