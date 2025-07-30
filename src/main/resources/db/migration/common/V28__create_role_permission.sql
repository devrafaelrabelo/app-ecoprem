CREATE SCHEMA IF NOT EXISTS security;

CREATE TABLE IF NOT EXISTS security.role_permission (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES security.role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES security.permission(id) ON DELETE CASCADE
);