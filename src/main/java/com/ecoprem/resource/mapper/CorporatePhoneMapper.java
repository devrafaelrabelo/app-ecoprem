package com.ecoprem.resource.mapper;

import com.ecoprem.entity.communication.CorporatePhone;
import com.ecoprem.entity.common.Company;
import com.ecoprem.entity.user.User;
import com.ecoprem.enums.CarrierType;
import com.ecoprem.enums.PhoneStatus;
import com.ecoprem.enums.PlanType;
import com.ecoprem.resource.dto.CorporatePhoneDTO;

public class CorporatePhoneMapper {

    public static CorporatePhoneDTO toDTO(CorporatePhone entity) {
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

    public static CorporatePhone toEntity(CorporatePhoneDTO dto, User user, Company company) {
        CorporatePhone phone = new CorporatePhone();
        phone.setId(dto.getId());
        phone.setNumber(dto.getNumber());
        phone.setCarrier(CarrierType.valueOf(dto.getCarrier().trim().toUpperCase()));
        phone.setPlanType(PlanType.valueOf(dto.getPlanType().trim().toUpperCase()));
        phone.setStatus(PhoneStatus.valueOf(dto.getStatus().trim().toUpperCase()));
        phone.setCurrentUser(user);
        phone.setCompany(company);
        return phone;
    }
}
