package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.AdminRoleCreateUpdateDTO;
import com.controlcenter.admin.dto.AdminRoleDTO;
import com.controlcenter.admin.dto.AdminRoleResponseDTO;
import com.controlcenter.admin.dto.AdminRoleWithCountDTO;
import com.controlcenter.admin.service.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    @Operation(summary = "List all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<AdminRoleWithCountDTO>> listAll() {
        return ResponseEntity.ok(adminRoleService.findAllWithCount());
    }

    @Operation(summary = "Get role by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<AdminRoleResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminRoleService.findById(id));
    }

    @Operation(
            summary = "Create a new role",
            description = "Creates a role with optional permissions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or role already exists", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<AdminRoleResponseDTO> create(
            @Valid @RequestBody AdminRoleCreateUpdateDTO dto) {
        return ResponseEntity.ok(adminRoleService.create(dto));
    }

    @Operation(summary = "Update an existing role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or name already in use", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<AdminRoleResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody AdminRoleCreateUpdateDTO dto) {
        return ResponseEntity.ok(adminRoleService.update(id, dto));
    }

    @Operation(summary = "Delete a role by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminRoleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
