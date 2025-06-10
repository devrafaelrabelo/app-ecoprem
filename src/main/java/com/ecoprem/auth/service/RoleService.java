package com.ecoprem.auth.service;

import com.ecoprem.auth.exception.InvalidRequestException;
import com.ecoprem.auth.exception.InvalidRoleAssignmentException;
import com.ecoprem.auth.exception.RoleNotFoundException;
import com.ecoprem.auth.repository.RoleRepository;
import com.ecoprem.entity.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private static final Set<String> ALLOWED_REGISTRATION_ROLES = Set.of("CLIENT", "BASIC_USER");

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
