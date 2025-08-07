package com.controlcenter.user.service;

import com.controlcenter.admin.repository.AdminRoleRepository;
import com.controlcenter.exceptions.exception.RoleNotFoundException;
import com.controlcenter.exceptions.exception.UserNotFoundException;
import com.controlcenter.auth.repository.RoleRepository;
import com.controlcenter.core.audit.service.SystemAuditLogService;
import com.controlcenter.exceptions.exception.ConflictException;
import com.controlcenter.entity.security.Role;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class UserRoleService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AdminRoleRepository adminRoleRepository;
    private SystemAuditLogService systemAuditLogService;

    @Transactional
    public void assignRoleToUser(UUID userId, UUID roleId, HttpServletRequest request, UUID adminId, String adminUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role não encontrada"));

        // Evitar duplicidade
        boolean alreadyAssigned = user.getRoles().contains(role);
        if (alreadyAssigned) {
            throw new ConflictException("Este papel já está atribuído ao usuário.");
        }

        user.getRoles().add(role);
        userRepository.save(user);

        systemAuditLogService.logAction(
                "ROLE_ASSIGNED",
                "User",
                user.getId().toString(),
                adminUsername,
                adminId,
                request,
                Map.of(
                        "assignedRole", role.getName(),
                        "targetUser", user.getEmail()
                )
        );
    }

    @Transactional
    public void revokeRoleFromUser(UUID userId, UUID roleId, HttpServletRequest request, UUID adminId, String adminUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role não encontrada"));

        if (!user.getRoles().contains(role)) {
            throw new ConflictException("Este papel não está atribuído ao usuário.");
        }

        user.getRoles().remove(role);
        userRepository.save(user);

        systemAuditLogService.logAction(
                "ROLE_REVOKED",
                "User",
                user.getId().toString(),
                adminUsername,
                adminId,
                request,
                Map.of(
                        "revokedRole", role.getName(),
                        "targetUser", user.getEmail()
                )
        );
    }
}
