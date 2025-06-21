package com.ecoprem.user.service;

import com.ecoprem.auth.dto.UserPermissionDTO;
import com.ecoprem.auth.repository.PermissionRepository;
import com.ecoprem.auth.repository.UserPermissionRepository;
import com.ecoprem.entity.security.Permission;
import com.ecoprem.entity.security.Role;
import com.ecoprem.entity.security.UserPermission;
import com.ecoprem.entity.user.User;
import com.ecoprem.user.dto.UserPermissionsResponse;
import com.ecoprem.user.dto.UserPermissionsResponse.MenuGroup;
import com.ecoprem.user.dto.UserPermissionsResponse.MenuItem;
import com.ecoprem.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void assignPermission(UserPermissionDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Permission permission = permissionRepository.findById(dto.permissionId())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        UserPermission userPermission = new UserPermission();
        userPermission.setId(UUID.randomUUID());
        userPermission.setUser(user);
        userPermission.setPermission(permission);

        userPermissionRepository.save(userPermission);
    }

    @Transactional
    public void revokePermission(UserPermissionDTO dto) {
        userPermissionRepository.deleteByUserIdAndPermissionId(dto.userId(), dto.permissionId());
    }

    public UserPermissionsResponse getPermissionsWithMenus(User user) {
        user = userRepository.findWithPermissions(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<String> directPermissions = user.getUserPermissions().stream()
                .map(up -> up.getPermission().getName())
                .collect(Collectors.toSet());

        Set<String> rolePermissions = user.getRoles().stream()
                .map(Role::getPermissions)
                .flatMap(Set::stream)
                .map(Permission::getName)
                .collect(Collectors.toSet());

        Set<String> effectivePermissions = new HashSet<>();
        effectivePermissions.addAll(directPermissions);
        effectivePermissions.addAll(rolePermissions);

        List<MenuGroup> allGroups = List.of(
                new MenuGroup("Usuários", "Users", List.of(
                        new MenuItem("Gerenciar Usuários", "User", "/users",
                                List.of("user:view"), List.of())
                )),
                new MenuGroup("Recursos", "Package", List.of(
                        new MenuItem("Recursos", "Package", "/resources",
                                List.of("resource:view"), List.of()),
                        new MenuItem("Tipos de Recurso", "Credit-card", "/resource-types",
                                List.of("resourcetype:view"), List.of()),
                        new MenuItem("Status de Recurso", "Multiple", "/resource-status",
                                List.of("resourcestatus:view"), List.of())
                )),
                new MenuGroup("Telefonia", "Phone", List.of(
                        new MenuItem("Telefones Corporativos", "Phone", "/corporate-phones",
                                List.of("corporate-phone:view"), List.of()),
                        new MenuItem("Ramais Internos", "Phone-forwarded", "/internal-extensions",
                                List.of("internal-extension:view"), List.of())
                )),
                new MenuGroup("Segurança", "Shield-check", List.of(
                        new MenuItem("Cargos e Permissões", "Shield-check", "/roles",
                                List.of("role:view", "permission:view"), List.of())
                ))
        );

        // Filtra apenas menus visíveis ao usuário
        List<MenuGroup> visibleGroups = allGroups.stream()
                .map(group -> {
                    List<MenuItem> visibleItems = group.submenu().stream()
                            .filter(menu -> effectivePermissions.containsAll(menu.requiredPermissions()))
                            .map(menu -> new MenuItem(
                                    menu.label(),
                                    menu.icon(),
                                    menu.path(),
                                    menu.requiredPermissions(),
                                    effectivePermissions.stream()
                                            .filter(p -> p.startsWith(getPrefix(menu)))
                                            .filter(p -> !menu.requiredPermissions().contains(p))
                                            .toList()
                            ))
                            .toList();
                    return new MenuGroup(group.title(), group.icon(), visibleItems);
                })
                .filter(group -> !group.submenu().isEmpty())
                .toList();

        return new UserPermissionsResponse(new ArrayList<>(effectivePermissions), visibleGroups);
    }

    private String getPrefix(MenuItem menu) {
        if (!menu.requiredPermissions().isEmpty()) {
            return menu.requiredPermissions().get(0).split(":")[0] + ":";
        }
        return "";
    }
}
