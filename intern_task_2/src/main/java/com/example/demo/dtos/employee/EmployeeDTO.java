package com.example.demo.dtos.employee;

import com.example.demo.dtos.company.CompanyDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO class for representing employee information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDTO {
    Long id;
    String name;
    String surname;
    String salary;
    String hiringDate;
    String job;
    CompanyDTO company;
}
