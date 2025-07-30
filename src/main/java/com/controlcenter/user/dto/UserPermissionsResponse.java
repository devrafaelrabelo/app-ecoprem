package com.controlcenter.user.dto;

import java.util.List;

public record UserPermissionsResponse(
        List<String> permissions,
        List<MenuGroup> menus
) {
    public record MenuGroup(
            String title,
            String icon,
            List<MenuItem> submenu
    ) {}

    public record MenuItem(
            String label,
            String icon,
            String path,
            List<String> systemNames,
            List<String> requiredPermissions,
            List<String> actions
    ) {}
}