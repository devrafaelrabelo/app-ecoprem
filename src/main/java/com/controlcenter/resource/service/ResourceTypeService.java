package com.controlcenter.resource.service;

import com.controlcenter.entity.resource.ResourceType;
import com.controlcenter.resource.dto.ResourceTypeDto;
import com.controlcenter.resource.repository.ResourceTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceTypeService {

    private final ResourceTypeRepository repository;

    public List<ResourceTypeDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public ResourceTypeDto findById(UUID id) {
        return toDto(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de recurso não encontrado")));
    }

    public ResourceTypeDto create(ResourceTypeDto dto) {
        if (repository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new IllegalArgumentException("Já existe um tipo com esse código.");
        }

        ResourceType entity = new ResourceType();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(UUID.randomUUID());

        return toDto(repository.save(entity));
    }

    public ResourceTypeDto update(UUID id, ResourceTypeDto dto) {
        ResourceType entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de recurso não encontrado"));

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.isActive());

        return toDto(repository.save(entity));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private ResourceTypeDto toDto(ResourceType entity) {
        ResourceTypeDto dto = new ResourceTypeDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}