package com.example.demo.mappers.impl;

import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.dtos.company.CompanyResponseDTO;
import com.example.demo.entities.Company;
import com.example.demo.mappers.CompanyMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CompanyMapperImpl implements CompanyMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public CompanyDTO toDto(Company company) {
        if (company == null) {
            return null;
        }

        CompanyResponseDTO companyDTO = new CompanyResponseDTO();
        companyDTO.setName(company.getName());
        companyDTO.setCountry(company.getCountry());
        companyDTO.setFoundationDate(company.getFoundationDate() != null ? company.getFoundationDate().format(DATE_FORMATTER) : null);
        companyDTO.setSelfLink(api + company.getId());
        return companyDTO;
    }

    @Override
    public Company toEntity(CompanyDTO dto) {
        if (dto == null) {
            return null;
        }

        Company company = new Company();
        company.setName(dto.getName());
        company.setCountry(dto.getCountry());
        company.setFoundationDate(dto.getFoundationDate() != null ? LocalDate.parse(dto.getFoundationDate(), DATE_FORMATTER) : null);
        return company;
    }
}
