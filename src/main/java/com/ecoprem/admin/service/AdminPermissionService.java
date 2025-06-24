package com.ecoprem.admin.service;

import com.ecoprem.admin.dto.AdminPermissionDTO;
import com.ecoprem.admin.repository.AdminPermissionRepository;
import com.ecoprem.core.exception.DuplicatePermissionNameException;
import com.ecoprem.core.exception.InvalidPermissionException;
import com.ecoprem.core.exception.PermissionNotFoundException;
import com.ecoprem.entity.security.Permission;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPermissionService {

    private final AdminPermissionRepository adminPermissionRepository;

    public List<AdminPermissionDTO> findAll() {
        return adminPermissionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AdminPermissionDTO findById(UUID id) {
        Permission permission = adminPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permissão não encontrada"));
        return toDTO(permission);
    }

    @Transactional
    public AdminPermissionDTO create(AdminPermissionDTO dto) {
        validateName(dto.getName());

        if (adminPermissionRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicatePermissionNameException("Já existe uma permissão com esse nome.");
        }

        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setName(dto.getName().trim());
        permission.setDescription(dto.getDescription());

        return toDTO(adminPermissionRepository.save(permission));
    }

    public List<AdminPermissionDTO> searchByName(String nameFragment) {
        return adminPermissionRepository.findByNameContainingIgnoreCase(nameFragment)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminPermissionDTO update(UUID id, AdminPermissionDTO updatedDto) {
        validateName(updatedDto.getName());

        Permission permission = adminPermissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permissão não encontrada"));

        if (!permission.getName().equalsIgnoreCase(updatedDto.getName()) &&
                adminPermissionRepository.existsByNameIgnoreCase(updatedDto.getName())) {
            throw new DuplicatePermissionNameException("Já existe outra permissão com esse nome.");
        }

        permission.setName(updatedDto.getName().trim());
        permission.setDescription(updatedDto.getDescription());

        return toDTO(adminPermissionRepository.save(permission));
    }

    @Transactional
    public void delete(UUID id) {
        if (!adminPermissionRepository.existsById(id)) {
            throw new PermissionNotFoundException("Permissão não encontrada para exclusão.");
        }
        adminPermissionRepository.deleteById(id);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidPermissionException("O nome da permissão não pode estar em branco.");
        }
    }

    private AdminPermissionDTO toDTO(Permission permission) {
        AdminPermissionDTO dto = new AdminPermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        return dto;
    }
}
