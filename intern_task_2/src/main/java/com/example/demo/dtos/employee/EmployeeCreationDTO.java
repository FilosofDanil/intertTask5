package com.example.demo.dtos.employee;

import com.example.demo.dtos.company.CompanyDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO class for creating a new employee.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreationDTO extends EmployeeDTO {
    @NotBlank(message = "Name is mandatory")
    String name;
    @NotBlank(message = "Surname is mandatory")
    String surname;
    @NotBlank(message = "Salary is mandatory")
    @Min(value = 0, message = "Salary may not be less than zero")
    String salary;
    @NotBlank(message = "Job is mandatory")
    String job;
    @NotNull(message = "Company may not be null")
    CompanyDTO company;

    /**
     * Constructor for EmployeeCreationDTO.
     *
     * @param name        The name of the employee
     * @param surname     The surname of the employee
     * @param salary      The salary of the employee
     * @param hiringDate  The hiring date of the employee
     * @param job         The job of the employee
     * @param company     The company the employee belongs to
     */
    public EmployeeCreationDTO(Long id, String name, String surname, String salary, String hiringDate, String job, CompanyDTO company) {
        super(id, name, surname, salary, hiringDate, job, company);
        this.name = name;
        this.surname = surname;
        this.salary = salary;
        this.job = job;
        this.company = company;
    }
}
