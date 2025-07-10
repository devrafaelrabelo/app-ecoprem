CREATE OR REPLACE VIEW security.effective_user_permissions AS
SELECT
    ur.user_id,
    rp.permission_id,
    p.name AS permission_name,
    'ROLE' AS origin,
    r.name AS role_name
FROM security.user_role ur
JOIN security.role r ON ur.role_id = r.id
JOIN security.role_permission rp ON rp.role_id = r.id
JOIN security.permission p ON rp.permission_id = p.id

UNION

SELECT
    up.user_id,
    up.permission_id,
    p.name AS permission_name,
    'DIRECT' AS origin,
    NULL AS role_name
FROM security.user_permission up
JOIN security.permission p ON up.permission_id = p.id;


--
---- Todas as permissões do usuário
--SELECT permission_name FROM security.effective_user_permissions WHERE user_id = '...';
--
---- Permissões obtidas via role
--SELECT * FROM security.effective_user_permissions WHERE user_id = '...' AND origin = 'ROLE';
--
---- Permissões diretas
--SELECT * FROM security.effective_user_permissions WHERE origin = 'DIRECT';