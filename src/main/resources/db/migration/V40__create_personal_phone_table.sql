CREATE TABLE personal_phone (
    id UUID PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    type VARCHAR(20),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE
);