package com.example.demo.services.impl;

import com.example.demo.entities.Employee;
import com.example.demo.exceptions.CompanyNotFoundException;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.services.ReportGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReportGeneratorImpl implements ReportGenerator {
    EmployeeRepository employeeRepository;

    CompanyRepository companyRepository;

    public ByteArrayInputStream generateReport(Long companyId, String name,
                                               String surname, Long salaryFrom, Long salaryTo) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(CSVFormat.EXCEL.getQuoteMode());
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


        List<Employee> employees = employeeRepository
                .getAllEmployeeWithFilters(companyId, name, surname, salaryFrom, salaryTo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8), format)) {
            csvPrinter.printRecord("Name", "Surname", "Salary", "Hiring Date", "Job", "Company");

            for (Employee employee : employees) {
                csvPrinter.printRecord(
                        employee.getName(),
                        employee.getSurname(),
                        employee.getSalary(),
                        employee.getHiringDate(),
                        employee.getJob(),
                        employee.getCompany().getName());
            }

            csvPrinter.flush();

        } catch (IOException e) {
            log.error("Something went wrong on server. " + e.getMessage());
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
