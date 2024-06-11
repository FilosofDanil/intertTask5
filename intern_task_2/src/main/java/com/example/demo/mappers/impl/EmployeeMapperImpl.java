package com.example.demo.mappers.impl;

import com.example.demo.dtos.employee.EmployeeCreationDTO;
import com.example.demo.dtos.employee.EmployeeDTO;
import com.example.demo.dtos.employee.EmployeeResponseDTO;
import com.example.demo.entities.Employee;
import com.example.demo.enums.Jobs;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.mappers.EmployeeMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeMapperImpl implements EmployeeMapper {
    static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    CompanyMapper companyMapper;

    @Override
    public EmployeeDTO toDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeResponseDTO employeeDTO = new EmployeeResponseDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setName(employee.getName());
        employeeDTO.setSurname(employee.getSurname());
        employeeDTO.setSalary(employee.getSalary() != null ? employee.getSalary().toString() : null);
        employeeDTO.setHiringDate(employee.getHiringDate() != null ? employee.getHiringDate().format(DATE_FORMATTER) : null);
        employeeDTO.setJob(employee.getJob() != null ? employee.getJob().toString() : null);
        employeeDTO.setCompany(employee.getCompany() != null ? companyMapper.toDto(employee.getCompany()) : null);
        employeeDTO.setSelfLink(idToLink(employee.getId()));

        return employeeDTO;
    }

    @Override
    public Employee toEntity(EmployeeDTO dto) {
        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setSurname(dto.getSurname());
        employee.setSalary(dto.getSalary() != null ? Integer.valueOf(dto.getSalary()) : null);
        employee.setHiringDate(dto.getHiringDate() != null ? LocalDate.parse(dto.getHiringDate(), DATE_FORMATTER) : null);
        employee.setJob(dto.getJob() != null ? Jobs.valueOf(dto.getJob()) : null);
        employee.setCompany(dto.getCompany() != null ? companyMapper.toEntity(dto.getCompany()) : null);

        return employee;
    }
}
