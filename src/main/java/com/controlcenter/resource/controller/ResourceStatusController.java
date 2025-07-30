package com.controlcenter.resource.controller;

import com.controlcenter.resource.dto.ResourceStatusDto;
import com.controlcenter.resource.service.ResourceStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resourcestatus")
@RequiredArgsConstructor
@Tag(name = "Status de Recurso", description = "Gerencia os status dos recursos do sistema")
public class ResourceStatusController {

    private final ResourceStatusService resourceStatusService;

    @GetMapping
    public ResponseEntity<List<ResourceStatusDto>> getAll() {
        return ResponseEntity.ok(resourceStatusService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceStatusDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(resourceStatusService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ResourceStatusDto> create(@Valid @RequestBody ResourceStatusDto dto) {
        return ResponseEntity.ok(resourceStatusService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceStatusDto> update(@PathVariable UUID id, @Valid @RequestBody ResourceStatusDto dto) {
        return ResponseEntity.ok(resourceStatusService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        resourceStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
