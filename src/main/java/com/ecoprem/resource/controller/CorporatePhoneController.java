package com.ecoprem.resource.controller;

import com.ecoprem.entity.communication.CorporatePhone;
import com.ecoprem.resource.dto.CorporatePhoneDTO;
import com.ecoprem.resource.mapper.CorporatePhoneMapper;
import com.ecoprem.resource.service.CorporatePhoneService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resource/corporate-phones")
@RequiredArgsConstructor
public class CorporatePhoneController {

    private final CorporatePhoneService corporatePhoneService;

    @GetMapping
    public ResponseEntity<Page<CorporatePhoneDTO>> findAll(Pageable pageable) {
        Page<CorporatePhone> page = corporatePhoneService.findAll(pageable);
        Page<CorporatePhoneDTO> dtoPage = page.map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorporatePhoneDTO> findById(@PathVariable UUID id) {
        CorporatePhone phone = corporatePhoneService.findById(id);
        return ResponseEntity.ok(toDTO(phone));
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CorporatePhoneDTO dto) {
        corporatePhoneService.create(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody CorporatePhoneDTO dto) {
        corporatePhoneService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        corporatePhoneService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CorporatePhoneDTO toDTO(CorporatePhone entity) {
        CorporatePhoneDTO dto = new CorporatePhoneDTO();
        dto.setId(entity.getId());
        dto.setNumber(entity.getNumber());
        dto.setCarrier(entity.getCarrier().name());
        dto.setPlanType(entity.getPlanType().name());
        dto.setStatus(entity.getStatus().name());
        dto.setCurrentUserId(entity.getCurrentUser() != null ? entity.getCurrentUser().getId() : null);
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        return dto;
    }
}