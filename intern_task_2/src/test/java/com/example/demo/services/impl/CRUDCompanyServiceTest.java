package com.example.demo.services.impl;

import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.entities.Company;
import com.example.demo.exceptions.NoContentPresentException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.mappers.impl.CompanyMapperImpl;
import com.example.demo.repositories.CompanyRepository;
import com.fasterxml.aalto.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CRUDCompanyServiceTest {

    @Mock
    private CompanyMapperImpl companyMapper;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CRUDCompanyService crudCompanyService;

    private List<Company> companies;

    @BeforeEach
    void setUp(){
        companies = new ArrayList<>();
        companies.add(Company.builder().name("Company1")
                .country("Ukraine").foundationDate(LocalDate.parse("2024-04-28")).build());
        companies.add(Company.builder().name("Company2")
                .country("Germany").foundationDate(LocalDate.parse("2024-04-28")).build());
    }

    @Test
    void getAllCompanies() {
        when(companyMapper.toDto(any(Company.class))).thenCallRealMethod();
        when(companyRepository.findAll()).thenReturn(companies);
        List<CompanyDTO> companyDTOS = crudCompanyService.getAll();
        assertEquals(2, companyDTOS.size());
        assertEquals("Company1", companyDTOS.get(0).getName());
        assertEquals("Company2", companyDTOS.get(1).getName());
    }

    @Test
    void getCompanyById() {
        when(companyMapper.toDto(any(Company.class))).thenCallRealMethod();
        when(companyRepository.findById(eq(1L)))
                .thenReturn(Optional.ofNullable(companies.get(0)));
        CompanyDTO companyDTO = crudCompanyService.getById(1L);
        assertEquals("Company1", companyDTO.getName());
        assertEquals("Ukraine", companyDTO.getCountry());
        assertEquals("2024-04-28", companyDTO.getFoundationDate());

    }

    @Test
    void getCompanyByIdInvalid() {
        when(companyRepository.findById(eq(3L)))
                .thenThrow(new ResourceNotFoundException("Cannot find company with id: 3"));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                crudCompanyService.getById(3L));
        assertEquals("Cannot find company with id: 3", exception.getMessage());
    }

    @Test
    void createCompany() {
        when(companyMapper.toDto(any(Company.class))).thenCallRealMethod();
        when(companyMapper.toEntity(any(CompanyDTO.class))).thenCallRealMethod();
        CompanyDTO newCompany = new CompanyDTO("Company3", "Ukraine", "2024-04-28");
        Company company = Company.builder()
                .id(3L)
                .name("Company3")
                .foundationDate(LocalDate.parse("2024-04-28"))
                .country("Ukraine")
                .build();
        when(companyRepository.save(any(Company.class)))
                .thenReturn(company);
        CompanyDTO companyDTO = crudCompanyService.create(newCompany);
        assertEquals("Company3", companyDTO.getName());
        assertEquals("Ukraine", companyDTO.getCountry());
        assertEquals("2024-04-28", companyDTO.getFoundationDate());
    }

    @Test
    void createCompanyInvalidDate() {
        when(companyMapper.toEntity(any(CompanyDTO.class))).thenCallRealMethod();
        CompanyDTO newCompany = new CompanyDTO("Company3", "Ukraine", "failure");
        DateTimeParseException exception = assertThrows(DateTimeParseException.class, () ->
                crudCompanyService.create(newCompany));
        assertEquals("Text 'failure' could not be parsed at index 0", exception.getMessage());
    }

    @Test
    void updateCompany() {
        when(companyMapper.toEntity(any())).thenCallRealMethod();
        when(companyRepository.existsById(1L)).thenReturn(true);
        CompanyDTO newCompany = new CompanyDTO("Company3", "Ukraine", "2024-04-28");
        crudCompanyService.update(newCompany, 1L);
        verify(companyRepository).updateCompany(
                eq(1L),
                eq(newCompany.getName()),
                eq(newCompany.getCountry()),
                eq(LocalDate.parse(newCompany.getFoundationDate()))
        );
    }

    @Test
    void updateCompanyNotFound() {
        CompanyDTO newCompany = new CompanyDTO("Company3", "Ukraine", "2024-04-28");

        when(companyRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> crudCompanyService.update(newCompany, 3L));

        verify(companyRepository).existsById(3L);
        verifyNoInteractions(companyMapper);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void deleteCompany() {
        when(companyRepository.existsById(1L)).thenReturn(true);
        crudCompanyService.delete(1L);
        verify(companyRepository)
                .deleteById(eq(1L));
    }

    @Test
    void deleteCompanyWithNoContent() {
        when(companyRepository.existsById(3L)).thenReturn(false);
        NoContentPresentException exception = assertThrows(NoContentPresentException.class, () ->
                crudCompanyService.delete(3L));
        assertEquals("No content present with id: 3", exception.getMessage());
    }
}