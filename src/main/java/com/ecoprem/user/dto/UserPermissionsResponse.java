package com.ecoprem.user.dto;

import java.util.List;

public record UserPermissionsResponse(
        List<String> permissions,
        List<MenuItem> menus
) {
    public record MenuItem(
            String label,
            String icon,
            String path,
            List<String> requiredPermissions,
            List<String> actions,// Permissões extras que o usuário tem além das mínimas
            String section // ← novo campo
    ) {}
}