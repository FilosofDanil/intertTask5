package com.example.demo.mappers;

import com.example.demo.dtos.employee.EmployeeDTO;
import com.example.demo.entities.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for converting Employee entities to DTOs and vice versa.
 */
@Mapper(componentModel = "spring", implementationName = "employeeMapper")
public interface EmployeeMapper {

    String api = "http://localhost:8080/api/employee/";

    /**
     * Converts an Employee entity to an EmployeeDTO.
     *
     * @param employee The Employee entity
     * @return The corresponding EmployeeDTO
     */
    @Mapping(target = "selfLink", source = "employee.id", qualifiedByName = "idToLink")
    EmployeeDTO toDto(Employee employee);

    /**
     * Converts an EmployeeDTO to an Employee entity.
     *
     * @param dto The EmployeeDTO
     * @return The corresponding Employee entity
     */
    Employee toEntity(EmployeeDTO dto);

    /**
     * Generates self link for a given employee id.
     *
     * @param id The id of the employee
     * @return The self link
     */
    @Named("idToLink")
    default String idToLink(Long id) {
        return api + id;
    }
}
