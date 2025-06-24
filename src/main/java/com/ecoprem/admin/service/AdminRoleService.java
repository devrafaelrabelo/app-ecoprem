package com.ecoprem.admin.service;

import com.ecoprem.admin.dto.AdminRoleCreateUpdateDTO;
import com.ecoprem.admin.dto.AdminRoleDTO;
import com.ecoprem.admin.dto.AdminRoleResponseDTO;
import com.ecoprem.admin.repository.AdminPermissionRepository;
import com.ecoprem.admin.repository.AdminRoleRepository;
import com.ecoprem.auth.exception.RoleNotFoundException;
import com.ecoprem.core.exception.PermissionNotFoundException;
import com.ecoprem.entity.security.Permission;
import com.ecoprem.entity.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final AdminRoleRepository adminRoleRepository;
    private final AdminPermissionRepository adminPermissionRepository;

    public AdminRoleResponseDTO findById(UUID id) {
        Role role = adminRoleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role com ID " + id + " não encontrada"));
        return toResponseDTO(role);
    }

    public List<AdminRoleDTO> findAll() {
        return adminRoleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AdminRoleResponseDTO create(AdminRoleCreateUpdateDTO dto) {
        validateRoleDTO(dto);

        if (adminRoleRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Já existe uma role com o nome informado.");
        }

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(dto.getName().trim());
        role.setDescription(dto.getDescription());
        role.setSystemRole(dto.isSystemRole());
        role.setPermissions(fetchPermissions(dto.getPermissionIds()));

        adminRoleRepository.save(role);
        return toResponseDTO(role);
    }

    public AdminRoleResponseDTO update(UUID id, AdminRoleCreateUpdateDTO dto) {
        validateRoleDTO(dto);

        Role role = adminRoleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role com ID " + id + " não encontrada"));

        if (!role.getName().equalsIgnoreCase(dto.getName()) &&
                adminRoleRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Nome de role já está em uso por outra.");
        }

        role.setName(dto.getName().trim());
        role.setDescription(dto.getDescription());
        role.setSystemRole(dto.isSystemRole());
        role.setPermissions(fetchPermissions(dto.getPermissionIds()));

        adminRoleRepository.save(role);
        return toResponseDTO(role);
    }

    public void delete(UUID id) {
        if (!adminRoleRepository.existsById(id)) {
            throw new RoleNotFoundException("Role com ID " + id + " não encontrada");
        }
        adminRoleRepository.deleteById(id);
    }

    private Set<Permission> fetchPermissions(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return Set.of();
        List<Permission> found = adminPermissionRepository.findAllById(ids);

        if (found.size() != ids.size()) {
            throw new PermissionNotFoundException("Uma ou mais permissões fornecidas são inválidas.");
        }

        return new HashSet<>(found);
    }

    private void validateRoleDTO(AdminRoleCreateUpdateDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da role é obrigatório.");
        }

        if (dto.getName().length() > 100) {
            throw new IllegalArgumentException("O nome da role deve ter no máximo 100 caracteres.");
        }

        if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            throw new IllegalArgumentException("A descrição da role deve ter no máximo 255 caracteres.");
        }
    }

    private AdminRoleResponseDTO toResponseDTO(Role role) {
        AdminRoleResponseDTO dto = new AdminRoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setSystemRole(role.isSystemRole());

        Set<AdminRoleResponseDTO.PermissionInfo> permissionInfos = role.getPermissions().stream()
                .map(permission -> {
                    AdminRoleResponseDTO.PermissionInfo p = new AdminRoleResponseDTO.PermissionInfo();
                    p.setId(permission.getId());
                    p.setName(permission.getName());
                    p.setDescription(permission.getDescription());
                    return p;
                }).collect(Collectors.toSet());

        dto.setPermissions(permissionInfos);
        return dto;
    }

    private AdminRoleDTO toDTO(Role role) {
        AdminRoleDTO dto = new AdminRoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setSystemRole(role.isSystemRole());
        return dto;
    }
}
