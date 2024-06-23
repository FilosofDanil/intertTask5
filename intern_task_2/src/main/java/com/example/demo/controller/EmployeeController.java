package com.example.demo.controller;

import com.example.demo.constants.EmailConstants;
import com.example.demo.dtos.Statistic;
import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.dtos.employee.EmployeeCreationDTO;
import com.example.demo.dtos.employee.EmployeeDTO;
import com.example.demo.message.EmailMessage;
import com.example.demo.services.CRUDService;
import com.example.demo.services.EmployeeService;
import com.example.demo.services.ReportGenerator;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Controller class for handling Employee related HTTP requests.
 */
@RestController
@RequestMapping("api/v1/employee")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeController {
    final CRUDService<EmployeeDTO> employeeCrudService;
    final EmployeeService employeeService;
    final ReportGenerator reportGenerator;
    @Value("${kafka.topic.emailReceived}")
    String emailReceivedTopic;
    final KafkaOperations<String, EmailMessage> kafkaOperations;
    /**
     * Retrieves all employees.
     *
     * @return ResponseEntity with a list of EmployeeDTOs in the body
     */
    @GetMapping("")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeCrudService.getAll());
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id The ID of the employee to retrieve
     * @return ResponseEntity with the retrieved EmployeeDTO in the body
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(employeeCrudService.getById(id));
    }

    /**
     * Creates a new employee.
     *
     * @param employeeDTO The EmployeeCreationDTO containing employee information
     * @return ResponseEntity with the created EmployeeDTO in the body
     */
    @PostMapping("")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeCreationDTO employeeDTO) {
        EmployeeDTO savedDTO = employeeCrudService.create(employeeDTO);
        EmailMessage emailMessage = EmailMessage
                .builder()
                .subject(EmailConstants.employeeSubject)
                .content(EmailConstants.employeeText + savedDTO.getName())
                .receivers(EmailConstants.mails)
                .sent(false)
                .build();
        kafkaOperations.send(emailReceivedTopic, "saved", emailMessage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeCrudService.create(employeeDTO));
    }

    /**
     * Uploads employees from a JSON file.
     *
     * @param file The JSON file containing employee data
     * @return ResponseEntity with a Statistic object containing the upload results
     */
    @PostMapping("/upload")
    public ResponseEntity<Statistic> uploadEmployee(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(employeeService.uploadEmployeeJSON(file));
    }

    /**
     * Retrieves a page of employees with optional filtering.
     *
     * @param companyId  The ID of the company to filter by
     * @param name       The name of the employee to filter by
     * @param surname    The surname of the employee to filter by
     * @param salaryFrom The minimum salary to filter by
     * @param salaryTo   The maximum salary to filter by
     * @param page       The page number
     * @param size       The number of employees per page
     * @return Page of EmployeeDTOs
     */
    @PostMapping("/_list")
    public Page<EmployeeDTO> getAllEmployeesWithPagination(@RequestParam(required = false) Long companyId,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(required = false) String surname,
                                                           @RequestParam(required = false) Long salaryFrom,
                                                           @RequestParam(required = false) Long salaryTo,
                                                           @RequestParam(defaultValue = "0") Integer page,
                                                           @RequestParam(defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeService.getAllEmployeesWithPagination(companyId, name, surname, salaryFrom, salaryTo, pageable);
    }

    /**
     * Generates a report of employees with optional filtering and returns it as a CSV file.
     *
     * @param companyId  The ID of the company to filter by
     * @param name       The name of the employee to filter by
     * @param surname    The surname of the employee to filter by
     * @param salaryFrom The minimum salary to filter by
     * @param salaryTo   The maximum salary to filter by
     * @return ResponseEntity with the generated CSV report as a file attachment
     */
    @PostMapping("/_report")
    public ResponseEntity<InputStreamResource> generateReport(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) Long salaryFrom,
            @RequestParam(required = false) Long salaryTo) {
        String fileName = "employees_report.csv";
        ByteArrayInputStream in = reportGenerator.generateReport(companyId, name, surname, salaryFrom, salaryTo);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    /**
     * Updates an existing employee.
     *
     * @param id          The ID of the employee to update
     * @param employeeDTO The updated EmployeeDTO
     * @return ResponseEntity indicating the success of the update operation
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEmployee(@PathVariable("id") Long id,
                                               @RequestBody EmployeeDTO employeeDTO) {
        employeeCrudService.update(employeeDTO, id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id The ID of the employee to delete
     * @return ResponseEntity indicating the success of the delete operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeCrudService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
