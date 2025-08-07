package com.controlcenter.common.controller;

import com.controlcenter.common.dto.AssignPositionRequest;
import com.controlcenter.common.dto.PositionRequestDTO;
import com.controlcenter.common.dto.PositionResponseDTO;
import com.controlcenter.common.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/positions")
@RequiredArgsConstructor
public class AdminPositionController {

    private final PositionService positionService;

    @PreAuthorize("hasAuthority('position:read')")
    @GetMapping
    @Operation(summary = "Listar cargos com filtro opcional por nome ou descrição")
    public ResponseEntity<List<PositionResponseDTO>> listAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(positionService.findByFilters(name, description));
    }

    @PreAuthorize("hasAuthority('position:read')")
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cargo por ID")
    public ResponseEntity<PositionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(positionService.findById(id));
    }

    @PreAuthorize("hasAuthority('position:create')")
    @PostMapping
    @Operation(summary = "Criar novo cargo")
    public ResponseEntity<PositionResponseDTO> create(@Valid @RequestBody PositionRequestDTO dto) {
        return ResponseEntity.ok(positionService.create(dto));
    }

    @PreAuthorize("hasAuthority('position:update')")
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cargo por ID")
    public ResponseEntity<PositionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody PositionRequestDTO dto) {
        return ResponseEntity.ok(positionService.update(id, dto));
    }

    @PreAuthorize("hasAuthority('position:delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cargo por ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('position:assign')")
    @PostMapping("/assign-position")
    @Operation(summary = "Atribuir um cargo a um usuário")
    public ResponseEntity<Void> assignPosition(@Valid @RequestBody AssignPositionRequest request) {
        positionService.assignPositionToUser(request.getUserId(), request.getPositionId());
        return ResponseEntity.ok().build();
    }
}