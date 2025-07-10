SET search_path TO security;

CREATE TABLE IF NOT EXISTS user_user_group (
    user_id UUID NOT NULL,
    user_group_id UUID NOT NULL,
    PRIMARY KEY (user_id, user_group_id),
    CONSTRAINT fk_user_user_group_user FOREIGN KEY (user_id) REFERENCES security.users(id),
    CONSTRAINT fk_user_user_group_group FOREIGN KEY (user_group_id) REFERENCES security.user_group(id)
);
