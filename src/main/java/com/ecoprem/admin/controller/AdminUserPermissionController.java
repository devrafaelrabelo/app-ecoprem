package com.ecoprem.admin.controller;

import com.ecoprem.auth.dto.UserPermissionDTO;
import com.ecoprem.user.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user-permissions")
@RequiredArgsConstructor
public class AdminUserPermissionController {

    private final UserPermissionService userPermissionService;

    @PostMapping
    public ResponseEntity<Void> assignPermission(@RequestBody UserPermissionDTO dto) {
        userPermissionService.assignPermission(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> revokePermission(@RequestBody UserPermissionDTO dto) {
        userPermissionService.revokePermission(dto);
        return ResponseEntity.noContent().build();
    }
}
