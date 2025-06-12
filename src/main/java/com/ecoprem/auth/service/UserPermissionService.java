package com.ecoprem.auth.service;

import com.ecoprem.auth.dto.UserPermissionDTO;
import com.ecoprem.auth.repository.UserPermissionRepository;
import com.ecoprem.user.repository.UserRepository;
import com.ecoprem.entity.user.User;
import com.ecoprem.entity.security.Permission;
import com.ecoprem.entity.security.UserPermission;
import com.ecoprem.auth.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
}
