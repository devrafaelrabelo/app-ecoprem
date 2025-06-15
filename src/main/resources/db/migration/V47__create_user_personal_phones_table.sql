CREATE TABLE user_personal_phones (
    user_id UUID NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    CONSTRAINT fk_user_personal_phones_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);