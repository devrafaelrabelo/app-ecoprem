package com.controlcenter.common.service;

import com.controlcenter.common.dto.AddressDTO;
import com.controlcenter.common.dto.CompanyDTO;
import com.controlcenter.common.dto.CreateCompanyDTO;
import com.controlcenter.common.dto.UpdateCompanyDTO;
import com.controlcenter.common.repository.CompanyRepository;
import com.controlcenter.entity.common.Address;
import com.controlcenter.entity.common.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyDTO> findAll() {
        return companyRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CompanyDTO findById(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        return toDTO(company);
    }

    public UUID create(CreateCompanyDTO dto) {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setName(dto.getName());
        company.setCnpj(dto.getCnpj());
        company.setLegalName(dto.getLegalName());
        company.setActive(dto.isActive());

        if (dto.getAddress() != null) {
            Address address = new Address();
            BeanUtils.copyProperties(dto.getAddress(), address);
            company.setAddress(address);
        }

        return companyRepository.save(company).getId();
    }

    public void update(UUID id, UpdateCompanyDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

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
        } else {
            company.setAddress(null);
        }

        companyRepository.save(company);
    }

    public void delete(UUID id) {
        companyRepository.deleteById(id);
    }

    private CompanyDTO toDTO(Company company) {
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
}
