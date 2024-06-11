package com.example.demo.services.impl;

import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.dtos.employee.EmployeeDTO;
import com.example.demo.entities.Company;
import com.example.demo.entities.Employee;
import com.example.demo.enums.Jobs;
import com.example.demo.exceptions.CompanyNotFoundException;
import com.example.demo.exceptions.JobNotPresentException;
import com.example.demo.exceptions.NoContentPresentException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.CompanyMapper;
import com.example.demo.mappers.impl.CompanyMapperImpl;
import com.example.demo.mappers.impl.EmployeeMapperImpl;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CRUDEmployeeServiceTest {

    private CompanyMapper companyMapper = new CompanyMapperImpl();

    @Spy
    private EmployeeMapperImpl employeeMapper = new EmployeeMapperImpl(companyMapper);

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployeeRepository employeeRepository;


    @InjectMocks
    private CRUDEmployeeService crudEmployeeService;

    private List<Employee> employees;

    @BeforeEach
    void setUp(){
        employees = new ArrayList<>();
        employees.add(new Employee(1L, "Danylo", "Berkovskyi", 70000, LocalDate.parse("2024-04-28"), Jobs.DEVOPS,
                new Company()));
        employees.add(new Employee(2L, "Jane", "Smith", 60000, LocalDate.parse("2024-04-28"), Jobs.DEVOPS,
                new Company()));
    }

    @Test
    void getAllEmployees() {
        when(employeeMapper.toDto(any(Employee.class))).thenCallRealMethod();
        when(employeeRepository.findAll()).thenReturn(employees);
        List<EmployeeDTO> employeeDTOS = crudEmployeeService.getAll();
        assertEquals(2, employeeDTOS.size());
        assertEquals("Danylo", employeeDTOS.get(0).getName());
        assertEquals("Jane", employeeDTOS.get(1).getName());
    }

    @Test
    void getEmployeeById() {
        when(employeeMapper.toDto(any(Employee.class))).thenCallRealMethod();
        when(employeeRepository.findById(eq(1L)))
                .thenReturn(Optional.ofNullable(employees.get(0)));
        EmployeeDTO employeeDTO = crudEmployeeService.getById(1L);
        assertEquals("Berkovskyi", employeeDTO.getSurname());
        assertEquals("Danylo", employeeDTO.getName());
        assertEquals("DEVOPS", employeeDTO.getJob());

    }

    @Test
    void getEmployeeByIdInvalid() {
        when(employeeRepository.findById(eq(3L)))
                .thenThrow(new ResourceNotFoundException("Cannot find employee with id: 3"));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                crudEmployeeService.getById(3L));
        assertEquals("Cannot find employee with id: 3", exception.getMessage());
    }

    @Test
    void createEmployee() {
        EmployeeDTO newEmployee = new EmployeeDTO(1L,"Danylo", "Berkovskyi","50000","2024-04-28", "DEVOPS",
                new CompanyDTO("Company3", "Ukraine", "2024-04-28"));
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(newEmployee);
        when(employeeMapper.toEntity(any(EmployeeDTO.class))).thenCallRealMethod();
        when(companyRepository.existsByName(anyString())).thenReturn(true);
        Employee employee = Employee.builder()
                .id(3L)
                .name("Danylo")
                .surname("Berkovskyi")
                .salary(50000)
                .job(Jobs.DEVOPS)
                .hiringDate(LocalDate.parse("2024-04-28"))
                .build();
        when(employeeRepository.insertEmployee(any(), any(), any(), any(), any(), any()))
                .thenReturn(employee);
        EmployeeDTO employeeDTO = crudEmployeeService.create(newEmployee);
        assertEquals("Berkovskyi", employeeDTO.getSurname());
        assertEquals("Danylo", employeeDTO.getName());
        assertEquals("DEVOPS", employeeDTO.getJob());
    }

    @Test
    void createEmployeeInvalidDate() {
        when(employeeMapper.toEntity(any(EmployeeDTO.class))).thenCallRealMethod();
        when(companyRepository.existsByName(anyString())).thenReturn(true);
        EmployeeDTO newEmployee = new EmployeeDTO(1l,"Danylo", "Berkovskyi","50000","failure", "DEVOPS",
                new CompanyDTO("Company3", "Ukraine", "2024-04-28"));
        DateTimeParseException exception = assertThrows(DateTimeParseException.class, () ->
                crudEmployeeService.create(newEmployee));
        assertEquals("Text 'failure' could not be parsed at index 0", exception.getMessage());
    }

    @Test
    void createEmployeeInvalidJob() {
        when(companyRepository.existsByName(anyString())).thenReturn(true);
        EmployeeDTO newEmployee = new EmployeeDTO(1l,"Danylo", "Berkovskyi","50000","2024-04-28", "failure",
                new CompanyDTO("Company3", "Ukraine", "2024-04-28"));
        JobNotPresentException exception = assertThrows(JobNotPresentException.class, () ->
                crudEmployeeService.create(newEmployee));
        assertEquals("Provided job is not exist. Operation has aborted", exception.getMessage());
    }

    @Test
    void createCompanyInvalidCompany() {
        EmployeeDTO newEmployee = new EmployeeDTO(1l,"Danylo", "Berkovskyi","50000","failure", "DEVOPS",
                new CompanyDTO());
        CompanyNotFoundException exception = assertThrows(CompanyNotFoundException.class, () ->
                crudEmployeeService.create(newEmployee));
        assertEquals("Provided company is not exist. Operation has aborted", exception.getMessage());
    }


    @Test
    void updateEmployee() {
        when(employeeMapper.toEntity(any())).thenCallRealMethod();
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(companyRepository.existsByName(anyString())).thenReturn(true);
        EmployeeDTO newEmployee = new EmployeeDTO(1l,"Danylo", "Berkovskyi","50000","2024-04-28", "DEVOPS",
                new CompanyDTO("Company3", "Ukraine", "2024-04-28"));
        crudEmployeeService.update(newEmployee, 1L);
        verify(employeeRepository).updateEmployee(
                eq(1L),
                eq(newEmployee.getName()),
                eq(newEmployee.getSurname()),
                eq(Integer.valueOf(newEmployee.getSalary())),
                eq(newEmployee.getHiringDate()),
                eq(newEmployee.getJob()),
                eq(newEmployee.getCompany().getName())
        );
    }

    @Test
    void updateEmployeeNotFound() {
        EmployeeDTO newEmployee = new EmployeeDTO(1l,"Danylo", "Berkovskyi","50000","2024-04-28", "DEVOPS",
                new CompanyDTO("Company3", "Ukraine", "2024-04-28"));

        when(employeeRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> crudEmployeeService.update(newEmployee, 3L));

        verify(employeeRepository).existsById(3L);
        verifyNoInteractions(employeeMapper);
        verifyNoMoreInteractions(employeeRepository);
}

    @Test
    void deleteEmployee() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        crudEmployeeService.delete(1L);
        verify(employeeRepository)
                .deleteById(eq(1L));
    }

    @Test
    void deleteEmployeeWithNoContent() {
        when(employeeRepository.existsById(3L)).thenReturn(false);
        NoContentPresentException exception = assertThrows(NoContentPresentException.class, () ->
                crudEmployeeService.delete(3L));
        assertEquals("No content present with id: 3", exception.getMessage());
    }
}