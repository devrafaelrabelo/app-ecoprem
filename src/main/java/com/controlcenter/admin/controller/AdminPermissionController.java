package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.AdminPermissionDTO;
import com.controlcenter.admin.service.AdminPermissionService;
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
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    @Operation(summary = "Listar todas as permissões do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permissões retornada com sucesso")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<List<AdminPermissionDTO>> listAll() {
        return ResponseEntity.ok(adminPermissionService.findAll());
    }

    @Operation(summary = "Buscar permissão por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Permissão não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<AdminPermissionDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminPermissionService.findById(id));
    }

    @Operation(
            summary = "Criar nova permissão",
            description = "Cria uma nova permissão com nome único e descrição opcional"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já existente", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<AdminPermissionDTO> create(
            @Valid @RequestBody AdminPermissionDTO dto) {
        return ResponseEntity.ok(adminPermissionService.create(dto));
    }

    @Operation(summary = "Atualizar permissão existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome duplicado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Permissão não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<AdminPermissionDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody AdminPermissionDTO dto) {
        return ResponseEntity.ok(adminPermissionService.update(id, dto));
    }

    @Operation(summary = "Excluir permissão por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permissão excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Permissão não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar permissões por nome (parcial)",
            description = "Retorna permissões que contenham o trecho informado no nome (busca case-insensitive)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permissões retornada com sucesso")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<List<AdminPermissionDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(adminPermissionService.searchByName(name));
    }

}
