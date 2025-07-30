package com.controlcenter.resource.controller;

import com.controlcenter.entity.communication.InternalExtension;
import com.controlcenter.resource.dto.InternalExtensionDTO;
import com.controlcenter.resource.service.InternalExtensionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resource/internal-extensions")
@RequiredArgsConstructor
@Tag(name = "Internal Extensions", description = "Gerenciamento de ramais internos")
public class InternalExtensionController {

    private final InternalExtensionService internalExtensionService;

    @GetMapping
    @Operation(summary = "Listar todos os ramais internos")
    public ResponseEntity<List<InternalExtensionDTO>> findAll() {
        List<InternalExtensionDTO> list = internalExtensionService.findAll().stream()
                .map(InternalExtensionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ramal interno por ID")
    public ResponseEntity<InternalExtensionDTO> findById(@PathVariable UUID id) {
        InternalExtension extension = internalExtensionService.findById(id);
        return ResponseEntity.ok(InternalExtensionDTO.fromEntity(extension));
    }

    @PostMapping
    @Operation(summary = "Criar um novo ramal interno")
    public ResponseEntity<Void> create(@RequestBody InternalExtensionDTO dto) {
        internalExtensionService.create(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ramal interno existente")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody InternalExtensionDTO dto) {
        dto.setId(id);
        internalExtensionService.update(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar ramal interno")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            internalExtensionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
