package com.example.demo.services.impl;

import com.example.demo.dtos.employee.EmployeeDTO;
import com.example.demo.dtos.Statistic;
import com.example.demo.entities.Company;
import com.example.demo.entities.Employee;
import com.example.demo.exceptions.CompanyNotFoundException;
import com.example.demo.mappers.EmployeeMapper;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.services.EmployeeService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;

    EmployeeMapper employeeMapper;

    CompanyRepository companyRepository;

    Validator validator;

    ObjectMapper objectMapper;

    @Override
    public Statistic uploadEmployeeJSON(MultipartFile file) {
        log.info("Started reading file with name: " + file.getOriginalFilename()
                + " and with approximate size(in bytes): " + file.getSize());
        Statistic statistic = new Statistic();
        try (InputStream inputStream = file.getInputStream()) {
            JsonFactory jsonFactory = new JsonFactory();

            try (JsonParser parser = jsonFactory.createParser(inputStream)) {
                if (parser.nextToken() != JsonToken.START_ARRAY) {
                    throw new JsonParseException(parser, "The file does not match the JSON format.");
                }
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    try {
                        EmployeeDTO employee = objectMapper.readValue(parser, EmployeeDTO.class);
                        Set<ConstraintViolation<EmployeeDTO>> violations = validator.validate(employee);
                        if (!violations.isEmpty()) {
                            statistic.setUnsuccessfullyWrittenRows(statistic.getUnsuccessfullyWrittenRows() + 1);
                            continue;
                        }
                        insertRowInDatabase(employee);
                        statistic.setSuccessfullyWrittenRows(statistic
                                .getSuccessfullyWrittenRows() + 1);

                    } catch (JsonParseException exception) {
                        statistic.setUnsuccessfullyWrittenRows(statistic
                                .getUnsuccessfullyWrittenRows() + 1);
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return statistic;
    }

    @Override
    public Page<EmployeeDTO> getAllEmployeesWithPagination(Long companyId, String name, String surname,
                                                           Long salaryFrom, Long salaryTo, Pageable pageable) {
        if(companyId!=null){
            if(!companyRepository.existsById(companyId)){
                throw new CompanyNotFoundException("No company present with id: " + companyId);
            }
        }
        if(salaryFrom!=null ){
            if (salaryFrom < 0) {
                throw new IllegalArgumentException("Salary may not be negative");
            }
        }
        if(salaryTo!=null ){
            if (salaryTo < 0) {
                throw new IllegalArgumentException("Salary may not be negative");
            }
        }
        if(salaryFrom!=null && salaryTo!=null){
            if (salaryTo <= salaryFrom) {
                throw new IllegalArgumentException("Salary from should be less than salary To");
            }
        }
        if(name.isBlank()){
            name = null;
        }
        if(surname.isBlank()){
            surname = null;
        }
        return employeeRepository
                .getAllEmployeePages(companyId, name, surname, salaryFrom, salaryTo, pageable)
                .map(employeeMapper::toDto);
    }

    private void insertRowInDatabase(EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.toEntity(employeeDTO);
        Optional<Company> company = companyRepository.findByName(employeeDTO.getCompany().getName());
        employee.setCompany(company.get());
        employeeRepository.save(employee);
    }
}
