package com.ecoprem.admin.service;

import com.ecoprem.admin.dto.AdminPermissionDTO;
import com.ecoprem.admin.repository.AdminPermissionRepository;
import com.ecoprem.entity.security.Permission;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPermissionService {

    private final AdminPermissionRepository adminRermissionRepository;

    public List<AdminPermissionDTO> findAll() {
        return adminRermissionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AdminPermissionDTO findById(UUID id) {
        Permission permission = adminRermissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada"));

        return toDTO(permission);
    }

    @Transactional
    public AdminPermissionDTO create(AdminPermissionDTO dto) {
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        return toDTO(adminRermissionRepository.save(permission));
    }

    @Transactional
    public AdminPermissionDTO update(UUID id, AdminPermissionDTO updatedDto) {
        Permission permission = adminRermissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        permission.setName(updatedDto.getName());
        permission.setDescription(updatedDto.getDescription());

        return toDTO(adminRermissionRepository.save(permission));
    }

    @Transactional
    public void delete(UUID id) {
        adminRermissionRepository.deleteById(id);
    }

    private AdminPermissionDTO toDTO(Permission permission) {
        AdminPermissionDTO dto = new AdminPermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        return dto;
    }
}
