package com.ecoprem.user.service;

import com.ecoprem.auth.dto.UserPermissionDTO;
import com.ecoprem.auth.repository.PermissionRepository;
import com.ecoprem.auth.repository.UserPermissionRepository;
import com.ecoprem.entity.security.Permission;
import com.ecoprem.entity.security.Role;
import com.ecoprem.entity.security.UserPermission;
import com.ecoprem.entity.user.User;
import com.ecoprem.user.dto.UserPermissionsResponse;
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

        List<MenuItem> allMenus = List.of(
                new MenuItem("Usuários", "user", "/users", List.of("user:view"), List.of(), "TI"),
                new MenuItem("Recursos", "box", "/resources", List.of("resource:view"), List.of(), "TI"),
                new MenuItem("Tipos de Recurso", "layers", "/resource-types", List.of("resourcetype:view"), List.of(), "TI"),
                new MenuItem("Status de Recurso", "check-circle", "/resource-status", List.of("resourcestatus:view"), List.of(), "TI"),
                new MenuItem("Permissões e Cargos", "shield", "/roles", List.of("role:view", "permission:view"), List.of(), "TI")
        );

        List<MenuItem> availableMenus = allMenus.stream()
                .filter(menu -> effectivePermissions.containsAll(menu.requiredPermissions()))
                .map(menu -> new MenuItem(
                        menu.label(),
                        menu.icon(),
                        menu.path(),
                        menu.requiredPermissions(),
                        effectivePermissions.stream()
                                .filter(p -> p.startsWith(getPrefix(menu)))
                                .filter(p -> !menu.requiredPermissions().contains(p))
                                .toList(),
                        menu.section() // ← agora inclui a seção
                ))
                .toList();

        return new UserPermissionsResponse(new ArrayList<>(effectivePermissions), availableMenus);
    }

    private String getPrefix(MenuItem menu) {
        if (!menu.requiredPermissions().isEmpty()) {
            return menu.requiredPermissions().get(0).split(":")[0] + ":";
        }
        return "";
    }
}
