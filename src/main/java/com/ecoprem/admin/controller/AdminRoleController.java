package com.ecoprem.admin.controller;

import com.ecoprem.admin.dto.AdminRoleCreateUpdateDTO;
import com.ecoprem.admin.dto.AdminRoleDTO;
import com.ecoprem.admin.dto.AdminRoleResponseDTO;
import com.ecoprem.admin.service.AdminRoleService;
import com.ecoprem.auth.dto.RoleResponse;
import com.ecoprem.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    @GetMapping
    public ResponseEntity<List<AdminRoleDTO>> listAll() {
        return ResponseEntity.ok(adminRoleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminRoleResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminRoleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdminRoleResponseDTO> create(@RequestBody AdminRoleCreateUpdateDTO dto) {
        return ResponseEntity.ok(adminRoleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminRoleResponseDTO> update(@PathVariable UUID id,
                                                       @RequestBody AdminRoleCreateUpdateDTO dto) {
        return ResponseEntity.ok(adminRoleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminRoleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}