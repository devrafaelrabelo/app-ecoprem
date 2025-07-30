package com.controlcenter.user.service;

import com.controlcenter.auth.dto.UserPermissionDTO;
import com.controlcenter.auth.exception.UserNotFoundException;
import com.controlcenter.auth.repository.PermissionRepository;
import com.controlcenter.auth.repository.UserPermissionRepository;
import com.controlcenter.core.audit.service.SystemAuditLogService;
import com.controlcenter.core.exception.ConflictException;
import com.controlcenter.core.exception.PermissionNotFoundException;
import com.controlcenter.entity.security.Permission;
import com.controlcenter.entity.security.Role;
import com.controlcenter.entity.security.UserPermission;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.dto.UserPermissionsResponse;
import com.controlcenter.user.dto.UserPermissionsResponse.MenuGroup;
import com.controlcenter.user.dto.UserPermissionsResponse.MenuItem;
import com.controlcenter.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static com.controlcenter.core.validation.ValidationUtils.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.controlcenter.core.validation.ValidationUtils.requireFound;

@Slf4j
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
                .orElseThrow(() -> new UserNotFoundException("User not found"));



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
                        new MenuItem("Gerenciar Usuários", "User", "/users", Collections.singletonList("TI"),
                                List.of("user:read"), List.of()),
                        new MenuItem("Criar Usuários", "User", "/users/gerenciar-solicitacao",Collections.singletonList("TI"),
                                List.of("user:read"), List.of()),
                        new MenuItem("Desligamento Usuários", "User", "/users/desligamento",Collections.singletonList("TI"),
                                List.of("user:read"), List.of())
                )),
                new MenuGroup("Recursos", "Package", List.of(
                        new MenuItem("Ativos/Passivos", "Package", "/resources", List.of("TI"),
                                List.of("resource:read"), List.of()),
                        new MenuItem("Tipos", "Credit-card", "/resource-types", List.of("TI"),
                                List.of("resourcetype:read"), List.of()),
                        new MenuItem("Status", "Multiple", "/resource-status", List.of("TI"),
                                List.of("resourcestatus:read"), List.of())
                )),

                new MenuGroup("Telefonia", "Phone", List.of(
                        new MenuItem("Corporativos", "Phone", "/corporate-phones", List.of("TI"),
                                List.of("corporate-phone:read"), List.of()),
                        new MenuItem("Ramais", "Phone-forwarded", "/internal-extensions", List.of("TI"),
                                List.of("internal-extension:read"), List.of())
                )),

                new MenuGroup("Segurança", "Shield-check", List.of(
                        new MenuItem("Roles", "Shield-check", "/roles", List.of("TI"),
                                List.of("role:read"), List.of()),
                        new MenuItem("Permissões", "Shield-check", "/permissions", List.of("TI"),
                                List.of("permission:read"), List.of())
                )),

                new MenuGroup("Configurações", "Settings", List.of(
                        new MenuItem("Configurações Gerais", "Settings", "/settings", List.of("TI"),
                                List.of("settings:read"), List.of()),
                        new MenuItem("Logs de Auditoria", "Shield-check", "/audit-logs", List.of("TI"),
                                List.of("auditlog:read"), List.of())
                )),

                new MenuGroup("Solicitações", "Clipboard-list", List.of(
                        new MenuItem("Solicitar Usuários", "User", "/users/solicitar-usuario", List.of("TI", "COMERCIAL", "RH"),
                                List.of("requestuser:read"), List.of()),
                        new MenuItem("Desligamento Usuários", "User", "/desligamento", List.of("RH"),
                                List.of("user:read"), List.of()),
                        new MenuItem("Desligamento Usuários", "User", "/desligamento", List.of( "COMERCIAL"),
                                List.of("user:read"), List.of())
                )),

                new MenuGroup("Gerenciar Maquinas", "computer", List.of(
                        new MenuItem("Inventário de Máquinas", "list-checks", "/computers/machine-inventory", List.of("TI"),
                                List.of("computer:read"), List.of()),
                        new MenuItem("Alocar/Desalocar Máquina", "user-check", "/computers/allocation", List.of("TI"),
                                List.of("computer:update"), List.of()),
                        new MenuItem("Histórico de Alocações", "clock-rotate-left", "/computers/history", List.of("TI"),
                                List.of("computer:read"), List.of()),
                        new MenuItem("Manutenção Programada", "calendar-cog", "/computers/maintenance", List.of("TI"),
                                List.of("computer:update"), List.of()),
                        new MenuItem("Status Operacional", "activity", "/computers/status", List.of("TI"),
                                List.of("computer:read"), List.of()),
                        new MenuItem("Alertas e Incidentes", "alert-triangle", "/computers/alerts", List.of("TI"),
                                List.of("computer:security"), List.of()),
                        new MenuItem("Auditoria de Acessos", "fingerprint", "/computers/audit", List.of("TI"),
                                List.of("computer:audit"), List.of()),
                        new MenuItem("Relatórios de Uso", "file-bar-chart", "/computers/reports", List.of("TI"),
                                List.of("computer:report"), List.of())
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
                                    menu.systemNames(),
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
