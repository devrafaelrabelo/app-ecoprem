package com.controlcenter.resource.controller;

import com.controlcenter.resource.dto.ResourceTypeDto;
import com.controlcenter.resource.service.ResourceTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resourcetype")
@RequiredArgsConstructor
@Tag(name = "Tipos de Recurso", description = "Gerencia os tipos de recursos do sistema")
public class ResourceTypeController {

    private final ResourceTypeService resourceTypeService;

    @GetMapping
    public ResponseEntity<List<ResourceTypeDto>> getAll() {
        return ResponseEntity.ok(resourceTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceTypeDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(resourceTypeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ResourceTypeDto> create(@Valid @RequestBody ResourceTypeDto dto) {
        return ResponseEntity.ok(resourceTypeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceTypeDto> update(@PathVariable UUID id, @Valid @RequestBody ResourceTypeDto dto) {
        return ResponseEntity.ok(resourceTypeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        resourceTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}