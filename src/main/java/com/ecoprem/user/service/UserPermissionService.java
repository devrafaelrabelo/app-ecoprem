package com.ecoprem.user.service;

import com.ecoprem.auth.dto.UserPermissionDTO;
import com.ecoprem.auth.repository.PermissionRepository;
import com.ecoprem.auth.repository.UserPermissionRepository;
import com.ecoprem.core.audit.service.SystemAuditLogService;
import com.ecoprem.core.exception.ConflictException;
import com.ecoprem.core.exception.PermissionNotFoundException;
import com.ecoprem.entity.security.Permission;
import com.ecoprem.entity.security.Role;
import com.ecoprem.entity.security.UserPermission;
import com.ecoprem.entity.user.User;
import com.ecoprem.user.dto.UserPermissionsResponse;
import com.ecoprem.user.dto.UserPermissionsResponse.MenuGroup;
import com.ecoprem.user.dto.UserPermissionsResponse.MenuItem;
import com.ecoprem.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.ecoprem.core.validation.ValidationUtils.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.ecoprem.core.validation.ValidationUtils.requireFound;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final SystemAuditLogService systemAuditLogService;

    @Transactional
    public void assignPermission(UserPermissionDTO dto, HttpServletRequest request, UUID adminId, String adminUsername) {
        User user = requireFound(
                userRepository.findById(dto.userId()),
                "Usuário não encontrado");

        Permission permission = requireFound(
                permissionRepository.findById(dto.permissionId()),
                "Permissão não encontrada");

        requireNot(
                userPermissionRepository.existsByUserIdAndPermissionId(user.getId(), permission.getId()),
                () -> new ConflictException("Permissão já atribuída a este usuário")
        );

        UserPermission userPermission = new UserPermission();
        userPermission.setUser(user);
        userPermission.setPermission(permission);
        userPermissionRepository.save(userPermission);

        systemAuditLogService.logAction(
                "PERMISSION_GRANTED",
                "User",
                user.getId().toString(),
                adminUsername,
                adminId,
                request,
                Map.of(
                        "permissionId", permission.getId(),
                        "permissionName", permission.getName(),
                        "targetUser", user.getEmail()
                )
        );
    }

    @Transactional
    public void revokePermission(UserPermissionDTO dto, HttpServletRequest request, UUID adminId, String adminUsername) {
        UserPermission up = requireFound(
                userPermissionRepository.findByUserIdAndPermissionId(dto.userId(), dto.permissionId()),
                "Permissão não atribuída ao usuário");

        userPermissionRepository.delete(up);

        systemAuditLogService.logAction(
                "PERMISSION_REVOKED",
                "User",
                dto.userId().toString(),
                adminUsername,
                adminId,
                request,
                Map.of(
                        "revokedPermission", dto.permissionId().toString(),
                        "targetUserId", dto.userId().toString()
                )
        );
    }

    @Transactional
    public void assignPermissionsInBatch(UUID userId, List<UUID> permissionIds, HttpServletRequest request,
                                         UUID adminId, String adminUsername) {

        User user = requireFound(userRepository.findById(userId), "Usuário não encontrado");

        Set<UUID> uniquePermissionIds = new HashSet<>(permissionIds);
        List<Permission> permissions = permissionRepository.findAllById(uniquePermissionIds);

        require(permissions.size() == uniquePermissionIds.size(),
                () -> new PermissionNotFoundException("Uma ou mais permissões não foram encontradas"));

        // Verificar quais permissões já estão atribuídas
        Set<UUID> alreadyAssigned = userPermissionRepository.findByUserId(userId).stream()
                .map(up -> up.getPermission().getId())
                .collect(Collectors.toSet());

        List<UUID> newPermissions = uniquePermissionIds.stream()
                .filter(id -> !alreadyAssigned.contains(id))
                .toList();

        for (UUID permissionId : newPermissions) {
            assignPermission(new UserPermissionDTO(userId, permissionId), request, adminId, adminUsername);
        }
    }

    @Transactional
    public void revokePermissionsInBatch(UUID userId, List<UUID> permissionIds, HttpServletRequest request,
                                         UUID adminId, String adminUsername) {

        User user = requireFound(userRepository.findById(userId), "Usuário não encontrado");

        Set<UUID> uniquePermissionIds = new HashSet<>(permissionIds);
        List<Permission> permissions = permissionRepository.findAllById(uniquePermissionIds);

        require(permissions.size() == uniquePermissionIds.size(),
                () -> new PermissionNotFoundException("Uma ou mais permissões não foram encontradas"));

        // Buscar permissões que realmente estão atribuídas
        Set<UUID> assignedPermissions = userPermissionRepository.findByUserId(userId).stream()
                .map(up -> up.getPermission().getId())
                .collect(Collectors.toSet());

        List<UUID> toRevoke = uniquePermissionIds.stream()
                .filter(assignedPermissions::contains)
                .toList();

        for (UUID permissionId : toRevoke) {
            revokePermission(new UserPermissionDTO(userId, permissionId), request, adminId, adminUsername);
        }
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
