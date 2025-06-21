package com.ecoprem.admin.controller;

import com.ecoprem.admin.dto.AdminPermissionDTO;
import com.ecoprem.admin.dto.AssignPermissionRequest;
import com.ecoprem.admin.service.AdminPermissionService;
import com.ecoprem.auth.dto.UserPermissionDTO;
import com.ecoprem.entity.user.User;
import com.ecoprem.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    @GetMapping
    public ResponseEntity<List<AdminPermissionDTO>> listAll() {
        return ResponseEntity.ok(adminPermissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminPermissionDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminPermissionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdminPermissionDTO> create(@RequestBody AdminPermissionDTO dto) {
        return ResponseEntity.ok(adminPermissionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminPermissionDTO> update(@PathVariable UUID id, @RequestBody AdminPermissionDTO dto) {
        return ResponseEntity.ok(adminPermissionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
