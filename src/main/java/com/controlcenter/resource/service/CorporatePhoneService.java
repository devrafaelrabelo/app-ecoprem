package com.controlcenter.resource.service;

import com.controlcenter.exceptions.exception.*;
import com.controlcenter.common.repository.CompanyRepository;
import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.communication.CorporatePhone;
import com.controlcenter.entity.user.User;
import com.controlcenter.enums.CarrierType;
import com.controlcenter.enums.PhoneStatus;
import com.controlcenter.enums.PlanType;
import com.controlcenter.resource.dto.CorporatePhoneDTO;
import com.controlcenter.resource.repository.CorporatePhoneRepository;
import com.controlcenter.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorporatePhoneService {

    private final CorporatePhoneRepository corporatePhoneRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public Page<CorporatePhone> findAll(Pageable pageable) {
        return corporatePhoneRepository.findAll(pageable);
    }

    public CorporatePhone findById(UUID id) {
        return corporatePhoneRepository.findById(id)
                .orElseThrow(() -> new CorporatePhoneNotFoundException("Telefone corporativo não encontrado com ID: " + id));
    }

    @Transactional
    public void create(CorporatePhoneDTO dto) {
        validateBeforeCreate(dto);

        UUID id = UUID.randomUUID();
        corporatePhoneRepository.insertManual(
                id,
                dto.getNumber(),
                parseCarrier(dto.getCarrier()).name(),
                parsePlanType(dto.getPlanType()).name(),
                parseStatus(dto.getStatus()).name(),
                dto.getCurrentUserId(),
                dto.getCompanyId(),
                LocalDateTime.now()
        );
    }

    @Transactional
    public void update(UUID id, CorporatePhoneDTO dto) {
        validateBeforeUpdate(id, dto);

        int updated = corporatePhoneRepository.updateManual(
                id,
                dto.getNumber(),
                parseCarrier(dto.getCarrier()).name(),
                parsePlanType(dto.getPlanType()).name(),
                parseStatus(dto.getStatus()).name(),
                dto.getCurrentUserId(),
                dto.getCompanyId(),
                LocalDateTime.now()
        );

        if (updated == 0) {
            throw new CorporatePhoneNotFoundException("Telefone corporativo com ID " + id + " não encontrado.");
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (!corporatePhoneRepository.existsById(id)) {
            throw new CorporatePhoneNotFoundException("Telefone corporativo não encontrado com ID: " + id);
        }
        corporatePhoneRepository.deleteById(id);
    }

    // --- Validações ---

    private void validateBeforeCreate(CorporatePhoneDTO dto) {
        validateCommon(dto);

        if (corporatePhoneRepository.existsByNumber(dto.getNumber())) {
            throw new DuplicateNumberPhoneException("Já existe um telefone com este número.");
        }
    }

    private void validateBeforeUpdate(UUID id, CorporatePhoneDTO dto) {
        if (!corporatePhoneRepository.existsById(id)) {
            throw new CorporatePhoneNotFoundException("Telefone corporativo com ID " + id + " não encontrado para atualização.");
        }
        validateCommon(dto);
    }

    private void validateCommon(CorporatePhoneDTO dto) {
        if (dto.getNumber() == null || dto.getNumber().isBlank()) {
            throw new InvalidPhoneException("Número do telefone é obrigatório.");
        }
        if (!dto.getNumber().matches("^\\+?\\d{10,15}$")) {
            throw new InvalidPhoneException("Número de telefone inválido.");
        }
        if (dto.getCarrier() == null || dto.getCarrier().isBlank()) {
            throw new InvalidCarrierException("Operadora não informada.");
        }
        if (dto.getPlanType() == null || dto.getPlanType().isBlank()) {
            throw new InvalidPlanTypeException("Tipo de plano não informado.");
        }
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new InvalidPhoneStatusException("Status do telefone não informado.");
        }
        if (dto.getCompanyId() != null && !companyRepository.existsById(dto.getCompanyId())) {
            throw new CompanyNotFoundException("Não encontrado." + dto.getCompanyId());
        }
        if (dto.getCurrentUserId() != null && !userRepository.existsById(dto.getCurrentUserId())) {
            throw new UserNotFoundException("Não encontrado." +  dto.getCurrentUserId());
        }
    }

    // --- Utilitários internos ---

    private CarrierType parseCarrier(String value) {
        try {
            return CarrierType.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new InvalidCarrierException("Operadora inválida: " + value);
        }
    }

    private PlanType parsePlanType(String value) {
        try {
            return PlanType.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new InvalidPhoneStatusException("Tipo de plano inválido: " + value);
        }
    }

    private PhoneStatus parseStatus(String value) {
        try {
            return PhoneStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new InvalidCarrierException("Status inválido: " + value);
        }
    }

    private CorporatePhone fromDTO(CorporatePhoneDTO dto) {
        CorporatePhone phone = new CorporatePhone();
        phone.setNumber(dto.getNumber());

        phone.setCarrier(parseCarrier(dto.getCarrier()));
        phone.setPlanType(parsePlanType(dto.getPlanType()));
        phone.setStatus(parseStatus(dto.getStatus()));

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException("Empresa não encontrada com ID: " + dto.getCompanyId()));
            phone.setCompany(company);
        }

        if (dto.getCurrentUserId() != null) {
            User user = userRepository.findById(dto.getCurrentUserId())
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + dto.getCurrentUserId()));
            phone.setCurrentUser(user);
        }

        return phone;
    }
}