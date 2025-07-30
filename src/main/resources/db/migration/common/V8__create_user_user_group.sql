CREATE SCHEMA IF NOT EXISTS "user";
SET search_path TO "user";

CREATE TABLE IF NOT EXISTS user_user_group (
    user_id UUID NOT NULL,
    user_group_id UUID NOT NULL,
    PRIMARY KEY (user_id, user_group_id),
    FOREIGN KEY (user_id) REFERENCES security.users(id) ON DELETE CASCADE,
    FOREIGN KEY (user_group_id) REFERENCES security.user_group(id) ON DELETE CASCADE
);