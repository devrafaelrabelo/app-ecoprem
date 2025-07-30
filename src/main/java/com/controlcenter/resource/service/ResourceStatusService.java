package com.controlcenter.resource.service;

import com.controlcenter.entity.resource.ResourceStatus;
import com.controlcenter.resource.dto.ResourceStatusDto;
import com.controlcenter.resource.repository.ResourceStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceStatusService {

    private final ResourceStatusRepository resourceStatusRepository;

    public List<ResourceStatusDto> findAll() {
        return resourceStatusRepository.findAll().stream().map(this::toDto).toList();
    }

    public ResourceStatusDto findById(UUID id) {
        return toDto(resourceStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Status de recurso não encontrado")));
    }

    public ResourceStatusDto create(ResourceStatusDto dto) {
        if (resourceStatusRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new IllegalArgumentException("Já existe um status com esse código.");
        }

        ResourceStatus entity = new ResourceStatus();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(UUID.randomUUID());

        return toDto(resourceStatusRepository.save(entity));
    }

    public ResourceStatusDto update(UUID id, ResourceStatusDto dto) {
        ResourceStatus entity = resourceStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Status de recurso não encontrado"));

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setBlocksAllocation(dto.isBlocksAllocation());

        return toDto(resourceStatusRepository.save(entity));
    }

    public void delete(UUID id) {
        resourceStatusRepository.deleteById(id);
    }

    private ResourceStatusDto toDto(ResourceStatus entity) {
        ResourceStatusDto dto = new ResourceStatusDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
