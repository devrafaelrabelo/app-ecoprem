package com.ecoprem.admin.controller;

import com.ecoprem.admin.dto.AdminRoleCreateUpdateDTO;
import com.ecoprem.admin.dto.AdminRoleDTO;
import com.ecoprem.admin.dto.AdminRoleResponseDTO;
import com.ecoprem.admin.service.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    @Operation(summary = "List all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<AdminRoleDTO>> listAll() {
        return ResponseEntity.ok(adminRoleService.findAll());
    }

    @Operation(summary = "Get role by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @GetMapping("/{id}")
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
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminRoleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
