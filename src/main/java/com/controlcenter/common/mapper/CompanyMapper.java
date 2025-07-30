package com.controlcenter.common.mapper;

import com.controlcenter.common.dto.CompanyDTO;
import com.controlcenter.common.dto.AddressDTO;
import com.controlcenter.entity.common.Address;
import com.controlcenter.entity.common.Company;

public class CompanyMapper {

    public static CompanyDTO toDTO(Company company) {
        if (company == null) return null;

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setCnpj(company.getCnpj());
        dto.setLegalName(company.getLegalName());
        dto.setActive(company.isActive());

        if (company.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(company.getAddress().getStreet());
            addressDTO.setNumber(company.getAddress().getNumber());
            addressDTO.setComplement(company.getAddress().getComplement());
            addressDTO.setNeighborhood(company.getAddress().getNeighborhood());
            addressDTO.setCity(company.getAddress().getCity());
            addressDTO.setState(company.getAddress().getState());
            addressDTO.setCountry(company.getAddress().getCountry());
            addressDTO.setPostalCode(company.getAddress().getPostalCode());
            dto.setAddress(addressDTO);
        }

        return dto;
    }

    public static Company fromDTO(CompanyDTO dto) {
        if (dto == null) return null;

        Company company = new Company();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setCnpj(dto.getCnpj());
        company.setLegalName(dto.getLegalName());
        company.setActive(dto.isActive());

        if (dto.getAddress() != null) {
            Address address = new Address();
            address.setStreet(dto.getAddress().getStreet());
            address.setNumber(dto.getAddress().getNumber());
            address.setComplement(dto.getAddress().getComplement());
            address.setNeighborhood(dto.getAddress().getNeighborhood());
            address.setCity(dto.getAddress().getCity());
            address.setState(dto.getAddress().getState());
            address.setCountry(dto.getAddress().getCountry());
            address.setPostalCode(dto.getAddress().getPostalCode());
            company.setAddress(address);
        }

        return company;
    }
}
