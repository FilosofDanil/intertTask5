package com.example.demo.dtos.employee;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO class for representing an employee in the response.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeResponseDTO extends EmployeeDTO {

    /**
     * The self link of the employee.
     */
    String selfLink;
}
