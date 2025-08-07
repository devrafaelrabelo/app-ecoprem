package com.controlcenter.resource.service;

import com.controlcenter.common.repository.CompanyRepository;
import com.controlcenter.entity.resource.Resource;
import com.controlcenter.entity.resource.ResourceStatus;
import com.controlcenter.entity.resource.ResourceType;
import com.controlcenter.exceptions.exception.*;
import com.controlcenter.entity.common.*;
import com.controlcenter.entity.user.User;
import com.controlcenter.resource.dto.*;
import com.controlcenter.resource.repository.*;
import com.controlcenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceStatusRepository statusRepository;
    private final CompanyRepository companyRepository;
    private final ResourceTypeRepository typeRepository;
    private final UserRepository userRepository;

    public Page<ResourceResponse> listAll(Pageable pageable) {
        return resourceRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<ResourceResponse> listByStatus(String code, Pageable pageable) {
        boolean exists = statusRepository.existsByCodeIgnoreCase(code);
        if (!exists) {
            throw new InvalidResourceStatusException("Código de status inválido: " + code);
        }

        return resourceRepository.findByStatus_Code(code, pageable).map(this::toResponse);
    }

    public Page<ResourceResponse> search(String query, Pageable pageable) {
        return resourceRepository.searchByText(query, pageable).map(this::toResponse);
    }

    public ResourceResponse getById(UUID id) {
        return resourceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado: " + id));
    }

    public ResourceResponse createResource(ResourceCreateRequest request) {
        Resource resource = Resource.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .assetTag(request.getAssetTag())
                .serialNumber(request.getSerialNumber())
                .brand(request.getBrand())
                .model(request.getModel())
                .price(request.getPrice())
                .purchaseDate(request.getPurchaseDate())
                .warrantyEndDate(request.getWarrantyEndDate())
                .location(request.getLocation())
                .responsibleSector(request.getResponsibleSector())
                .status(fetchStatus(request.getStatusId()))
                .company(fetchCompany(request.getCompanyId()))
                .resourceType(fetchType(request.getResourceTypeId()))
                .currentUser(fetchUser(request.getCurrentUserId()))
                .build();

        return toResponse(resourceRepository.save(resource));
    }

    public ResourceResponse updateResource(UUID id, ResourceUpdateRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado: " + id));

        resource.setName(request.getName());
        resource.setAssetTag(request.getAssetTag());
        resource.setSerialNumber(request.getSerialNumber());
        resource.setBrand(request.getBrand());
        resource.setModel(request.getModel());
        resource.setPrice(request.getPrice());
        resource.setPurchaseDate(request.getPurchaseDate());
        resource.setWarrantyEndDate(request.getWarrantyEndDate());
        resource.setLocation(request.getLocation());
        resource.setResponsibleSector(request.getResponsibleSector());
        resource.setStatus(fetchStatus(request.getStatusId()));
        resource.setCompany(fetchCompany(request.getCompanyId()));
        resource.setResourceType(fetchType(request.getResourceTypeId()));
        resource.setCurrentUser(fetchUser(request.getCurrentUserId()));

        return toResponse(resourceRepository.save(resource));
    }

    public void deleteById(UUID id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado para exclusão: " + id);
        }
        resourceRepository.deleteById(id);
    }

    private ResourceStatus fetchStatus(UUID id) {
        return id == null ? null :
                statusRepository.findById(id).orElseThrow(() ->
                        new InvalidResourceStatusException("Status inválido: " + id));
    }

    private Company fetchCompany(UUID id) {
        return id == null ? null :
                companyRepository.findById(id).orElseThrow(() ->
                        new InvalidCompanyException("Empresa inválida: " + id));
    }

    private ResourceType fetchType(UUID id) {
        return id == null ? null :
                typeRepository.findById(id).orElseThrow(() ->
                        new InvalidResourceTypeException("Tipo de recurso inválido: " + id));
    }

    private User fetchUser(UUID id) {
        return id == null ? null :
                userRepository.findById(id).orElseThrow(() ->
                        new InvalidUserException("Usuário inválido: " + id));
    }

    private ResourceResponse toResponse(Resource r) {
        return ResourceResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .assetTag(r.getAssetTag())
                .serialNumber(r.getSerialNumber())
                .brand(r.getBrand())
                .model(r.getModel())
                .status(r.getStatus() != null ? r.getStatus().getName() : null)
                .type(r.getResourceType() != null ? r.getResourceType().getName() : null)
                .company(r.getCompany() != null ? r.getCompany().getName() : null)
                .currentUser(r.getCurrentUser() != null ? r.getCurrentUser().getFullName() : null)
                .location(r.getLocation())
                .build();
    }
}
