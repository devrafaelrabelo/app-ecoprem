package com.controlcenter.resource.dto;

import com.controlcenter.entity.communication.InternalExtension;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InternalExtensionDTO {
    private UUID id;
    private String extension;
    private String application;
    private UUID currentUserId;
    private UUID companyId;
    private LocalDateTime lastUpdated;

    public static InternalExtensionDTO fromEntity(InternalExtension extension) {
        InternalExtensionDTO dto = new InternalExtensionDTO();
        dto.setId(extension.getId());
        dto.setExtension(extension.getExtension());
        dto.setApplication(extension.getApplication());
        dto.setLastUpdated(extension.getLastUpdated());

        if (extension.getCurrentUser() != null) {
            dto.setCurrentUserId(extension.getCurrentUser().getId());
        }
        if (extension.getCompany() != null) {
            dto.setCompanyId(extension.getCompany().getId());
        }
        return dto;
    }
}
