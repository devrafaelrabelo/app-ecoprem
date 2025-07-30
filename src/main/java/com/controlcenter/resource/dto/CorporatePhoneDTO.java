package com.controlcenter.resource.dto;

import com.controlcenter.entity.communication.CorporatePhone;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CorporatePhoneDTO {
    private UUID id;
    private String number;
    private String carrier;
    private String planType;
    private String status;
    private UUID companyId;
    private UUID currentUserId;
    private LocalDateTime lastUpdated;

    public static CorporatePhoneDTO fromEntity(CorporatePhone phone) {
        CorporatePhoneDTO dto = new CorporatePhoneDTO();
        dto.setId(phone.getId());
        dto.setNumber(phone.getNumber());
        dto.setCarrier(phone.getCarrier().name());
        dto.setPlanType(phone.getPlanType().name());
        dto.setStatus(phone.getStatus().name());
        dto.setLastUpdated(phone.getLastUpdated());

        if (phone.getCompany() != null) {
            dto.setCompanyId(phone.getCompany().getId());
        }

        if (phone.getCurrentUser() != null) {
            dto.setCurrentUserId(phone.getCurrentUser().getId());
        }

        return dto;
    }
}
