package com.ecoprem.resource.service;

import com.ecoprem.auth.exception.UserNotFoundException;
import com.ecoprem.core.repository.CompanyRepository;
import com.ecoprem.entity.communication.InternalExtension;
import com.ecoprem.entity.common.Company;
import com.ecoprem.entity.user.User;
import com.ecoprem.resource.dto.InternalExtensionDTO;
import com.ecoprem.resource.exception.CompanyNotFoundException;
import com.ecoprem.resource.exception.InternalExtensionException;
import com.ecoprem.resource.exception.InternalExtensionValidationException;
import com.ecoprem.resource.repository.InternalExtensionRepository;
import com.ecoprem.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalExtensionService {

    private final InternalExtensionRepository internalExtensionRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public List<InternalExtension> findAll() {
        return internalExtensionRepository.findAll();
    }

    public InternalExtension findById(UUID id) {
        return internalExtensionRepository.findById(id)
                .orElseThrow(() -> new InternalExtensionException("Ramal interno não encontrado com ID: " + id));
    }

    @Transactional
    public void create(InternalExtensionDTO dto) {
        validateBeforePersist(dto);

        InternalExtension extension = new InternalExtension();
        extension.setId(UUID.randomUUID());
        applyDTO(dto, extension);
        internalExtensionRepository.save(extension);
    }

    @Transactional
    public void update(InternalExtensionDTO dto) {
        validateBeforePersist(dto);

        InternalExtension extension = internalExtensionRepository.findById(dto.getId())
                .orElseThrow(() -> new InternalExtensionException("Ramal interno não encontrado com ID: " + dto.getId()));

        applyDTO(dto, extension);
        internalExtensionRepository.save(extension);
    }

    @Transactional
    public void delete(UUID id) {
        InternalExtension extension = internalExtensionRepository.findById(id)
                .orElseThrow(() -> new InternalExtensionException("Ramal interno não encontrado com ID: " + id));
        internalExtensionRepository.delete(extension);
    }

    private void applyDTO(InternalExtensionDTO dto, InternalExtension extension) {
        extension.setExtension(dto.getExtension());
        extension.setApplication(dto.getApplication());

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException("Empresa não encontrada com ID: " + dto.getCompanyId()));
            extension.setCompany(company);
        }

        if (dto.getCurrentUserId() != null) {
            User user = userRepository.findById(dto.getCurrentUserId())
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + dto.getCurrentUserId()));
            extension.setCurrentUser(user);
        } else {
            extension.setCurrentUser(null); // desvincular
        }
    }

    private void validateBeforePersist(InternalExtensionDTO dto) {
        if (dto.getExtension() == null || dto.getExtension().isBlank()) {
            throw new InternalExtensionValidationException("O campo 'extension' é obrigatório.");
        }

        if (dto.getExtension().length() > 20) {
            throw new InternalExtensionValidationException("O campo 'extension' não pode ter mais que 20 caracteres.");
        }

        if (dto.getApplication() != null && dto.getApplication().length() > 50) {
            throw new InternalExtensionValidationException("O campo 'application' não pode ter mais que 50 caracteres.");
        }
    }
}
