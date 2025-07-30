package com.controlcenter.resource.controller;

import com.controlcenter.entity.communication.CorporatePhone;
import com.controlcenter.resource.dto.CorporatePhoneDTO;
import com.controlcenter.resource.service.CorporatePhoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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