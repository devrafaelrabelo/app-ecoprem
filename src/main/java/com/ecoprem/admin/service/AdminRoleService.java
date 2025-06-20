package com.ecoprem.admin.service;

import com.ecoprem.admin.dto.AdminRoleCreateUpdateDTO;
import com.ecoprem.admin.dto.AdminRoleDTO;
import com.ecoprem.admin.dto.AdminRoleResponseDTO;
import com.ecoprem.admin.repository.AdminPermissionRepository;
import com.ecoprem.admin.repository.AdminRoleRepository;
import com.ecoprem.entity.security.Permission;
import com.ecoprem.entity.security.Role;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));
        return toResponseDTO(role);
    }

    public List<AdminRoleDTO> findAll() {
        return adminRoleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    public AdminRoleResponseDTO create(AdminRoleCreateUpdateDTO dto) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setSystemRole(dto.isSystemRole());

        Set<Permission> permissions = fetchPermissions(dto.getPermissionIds());
        role.setPermissions(permissions);

        adminRoleRepository.save(role);
        return toResponseDTO(role);
    }

    public AdminRoleResponseDTO update(UUID id, AdminRoleCreateUpdateDTO dto) {
        Role role = adminRoleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setSystemRole(dto.isSystemRole());

        Set<Permission> permissions = fetchPermissions(dto.getPermissionIds());
        role.setPermissions(permissions);

        adminRoleRepository.save(role);
        return toResponseDTO(role);
    }

    public void delete(UUID id) {
        adminRoleRepository.deleteById(id);
    }

    private Set<Permission> fetchPermissions(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return Set.of();
        return new HashSet<>(adminPermissionRepository.findAllById(ids));
    }

    private AdminRoleResponseDTO toResponseDTO(Role role) {
        AdminRoleResponseDTO dto = new AdminRoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setSystemRole(role.isSystemRole());

        Set<AdminRoleResponseDTO.PermissionInfo> permissionInfos = role.getPermissions().stream().map(permission -> {
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