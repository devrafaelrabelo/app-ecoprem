SET search_path TO security;

CREATE TABLE IF NOT EXISTS user_permission (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    granted_by UUID,
    granted_at TIMESTAMP,
    description TEXT,

    CONSTRAINT fk_user_permission_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_permission_permission FOREIGN KEY (permission_id) REFERENCES permission(id),
    CONSTRAINT fk_user_permission_granted_by FOREIGN KEY (granted_by) REFERENCES users(id)
);
