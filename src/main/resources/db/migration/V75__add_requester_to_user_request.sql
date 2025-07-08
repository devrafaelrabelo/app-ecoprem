ALTER TABLE user_request
ADD COLUMN requester_id UUID REFERENCES users(id);