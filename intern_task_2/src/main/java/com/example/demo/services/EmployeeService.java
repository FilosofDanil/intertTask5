package com.example.demo.services;

import com.example.demo.dtos.Statistic;
import com.example.demo.dtos.employee.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing employees.
 */
public interface EmployeeService {

    /**
     * Uploads employees from a JSON file.
     *
     * @param file The JSON file containing employee data
     * @return Statistic object containing information about the upload process
     */
    Statistic uploadEmployeeJSON(MultipartFile file);

    /**
     * Retrieves a page of employees with optional filters.
     *
     * @param companyId  The ID of the company to filter employees by
     * @param name       The name to filter employees by
     * @param surname    The surname to filter employees by
     * @param salaryFrom The minimum salary to filter employees by
     * @param salaryTo   The maximum salary to filter employees by
     * @param pageable   Pageable object for pagination and sorting
     * @return A page of EmployeeDTO objects matching the criteria
     */
    Page<EmployeeDTO> getAllEmployeesWithPagination(Long companyId, String name,
                                                    String surname, Long salaryFrom, Long salaryTo, Pageable pageable);
}
