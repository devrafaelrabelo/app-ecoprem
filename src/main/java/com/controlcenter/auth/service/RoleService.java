package com.controlcenter.auth.service;

import com.controlcenter.auth.dto.RoleResponse;
import com.controlcenter.exceptions.exception.InvalidRequestException;
import com.controlcenter.exceptions.exception.InvalidRoleAssignmentException;
import com.controlcenter.exceptions.exception.RoleNotFoundException;
import com.controlcenter.auth.repository.RoleRepository;
import com.controlcenter.entity.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private static final Set<String> ALLOWED_REGISTRATION_ROLES = Set.of("CLIENT", "BASIC_USER");

    public List<RoleResponse> listAll() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(
                        role.getId(),
                        role.getName(),
                        role.getDescription(),
                        role.isSystemRole()
                ))
                .toList();
    }

    public List<Role> resolveAndValidateRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new InvalidRequestException("É necessário informar pelo menos um role.");
        }

        List<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new InvalidRequestException("Um ou mais roles são inválidos ou inexistentes.");
        }

        for (String role : roleNames) {
            if (!ALLOWED_REGISTRATION_ROLES.contains(role.toUpperCase())) {
                throw new InvalidRoleAssignmentException("Você não tem permissão para registrar o papel: " + role);
            }
        }

        return roles;
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role não encontrada: " + name));
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
