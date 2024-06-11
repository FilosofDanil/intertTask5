package com.example.demo.services.impl;


import com.example.demo.dtos.employee.EmployeeDTO;
import com.example.demo.entities.Company;
import com.example.demo.entities.Employee;
import com.example.demo.enums.Jobs;
import com.example.demo.exceptions.CompanyNotFoundException;
import com.example.demo.exceptions.JobNotPresentException;
import com.example.demo.exceptions.NoContentPresentException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.EmployeeMapper;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.services.CRUDService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CRUDEmployeeService implements CRUDService<EmployeeDTO> {
    EmployeeMapper employeeMapper;

    EmployeeRepository employeeRepository;

    CompanyRepository companyRepository;

    @Override
    public List<EmployeeDTO> getAll() {
        log.info("Getting all employees");
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public EmployeeDTO getById(Long id) {
        log.info("Getting employee by by id: {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            throw new ResourceNotFoundException("Cannot find employee with id: " + id);
        }
        return employee.map(employeeMapper::toDto).get();
    }

    @Transactional
    @Override
    public EmployeeDTO create(EmployeeDTO employeeDTO) {
        log.info("Creating new employee.");
        if(!companyRepository.existsByName(employeeDTO.getCompany().getName())){
            throw new CompanyNotFoundException("Provided company is not exist. Operation has aborted");
        }
        if(!containsJob(employeeDTO.getJob())){
            throw new JobNotPresentException("Provided job is not exist. Operation has aborted");
        }
        Employee employee = employeeMapper.toEntity(employeeDTO);


        Employee savedEmployee = employeeRepository.insertEmployee(employee.getName(), employee.getSurname(),
                employee.getSalary(), employee.getHiringDate(), employee.getJob().toString(),
                employee.getCompany().getName());
        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    @Override
    public void update(EmployeeDTO employeeDTO, Long id) {
        log.info("Updating employee with id: {}", id);
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find employee with id: " + id);
        }
        Employee employee = employeeMapper.toEntity(employeeDTO);
        Optional<Company> company = Optional.ofNullable(employee.getCompany());
        if(company.isPresent()){
            if(!companyRepository.existsByName(company.get().getName())){
                throw new CompanyNotFoundException("Provided company is not exist. Operation has aborted");
            }
        }
        employeeRepository.updateEmployee(id, employee.getName(), employee.getSurname(),
                employee.getSalary(), employeeDTO.getHiringDate(), employeeDTO.getJob(),
                company.map(Company::getName).orElse(null));
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting employee with id: {}", id);
        if (!employeeRepository.existsById(id)) {
            throw new NoContentPresentException("No content present with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private boolean containsJob(String job){
        return Arrays
                .stream(Jobs.values())
                .map(jobs -> jobs.toString())
                .toList()
                .contains(job);
    }
}
