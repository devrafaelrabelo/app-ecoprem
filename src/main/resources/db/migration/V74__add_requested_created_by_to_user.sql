ALTER TABLE users
ADD COLUMN requested_by_id UUID REFERENCES users(id);

ALTER TABLE users
ADD COLUMN created_by_id UUID REFERENCES users(id);
