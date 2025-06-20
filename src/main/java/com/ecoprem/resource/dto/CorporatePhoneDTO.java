package com.ecoprem.resource.dto;

import com.ecoprem.entity.communication.CorporatePhone;
import lombok.Data;

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

    public static CorporatePhoneDTO fromEntity(CorporatePhone phone) {

        System.out.println("🚨 ENTROU NO fromEntity()");
        System.out.println("DTO recebido: carrier=" + phone.getCarrier().name());


        CorporatePhoneDTO dto = new CorporatePhoneDTO();
        dto.setId(phone.getId());
        dto.setNumber(phone.getNumber());
        dto.setCarrier(phone.getCarrier().name());
        dto.setPlanType(phone.getPlanType().name());
        dto.setStatus(phone.getStatus().name());



        if (phone.getCompany() != null) {
            dto.setCompanyId(phone.getCompany().getId());
        }

        if (phone.getCurrentUser() != null) {
            dto.setCurrentUserId(phone.getCurrentUser().getId());
        }

        return dto;
    }
}
