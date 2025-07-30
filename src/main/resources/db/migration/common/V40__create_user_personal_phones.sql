SET search_path TO "user";

CREATE TABLE IF NOT EXISTS user_personal_phones (
    user_id UUID NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    CONSTRAINT fk_user_personal_phones_user
        FOREIGN KEY (user_id) REFERENCES security.users(id) ON DELETE CASCADE
);
