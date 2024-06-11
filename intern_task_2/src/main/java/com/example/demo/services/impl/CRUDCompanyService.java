package com.example.demo.services.impl;

import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.entities.Company;
import com.example.demo.exceptions.NoContentPresentException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.services.CRUDService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CRUDCompanyService implements CRUDService<CompanyDTO> {
    CompanyMapper companyMapper;

    CompanyRepository companyRepository;

    @Override
    public List<CompanyDTO> getAll() {
        log.info("Getting all companies");
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toDto)
                .toList();
    }

    @Override
    public CompanyDTO getById(Long id) {
        log.info("Getting company by by id: {}", id);
        Optional<Company> company = companyRepository.findById(id);
        if (company.isEmpty()) {
            throw new ResourceNotFoundException("Cannot find company with id: " + id);
        }
        return company.map(companyMapper::toDto).get();
    }

    @Override
    public CompanyDTO create(CompanyDTO companyDTO) {
        log.info("Creating new company.");
        Company savedCompany = companyRepository.save(companyMapper.toEntity(companyDTO));
        return companyMapper.toDto(savedCompany);
    }

    @Transactional
    @Override
    public void update(CompanyDTO companyDTO, Long id) {
        log.info("Updating company with id: {}", id);
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find company with id: " + id);
        }
        Company company= companyMapper.toEntity(companyDTO);
        companyRepository.updateCompany(id, company.getName(),
                company.getCountry(), company.getFoundationDate());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting company with id: {}", id);
        if (!companyRepository.existsById(id)) {
            throw new NoContentPresentException("No content present with id: " + id);
        }
        companyRepository.deleteById(id);
    }
}
