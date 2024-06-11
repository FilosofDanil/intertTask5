package com.example.demo.services;

import java.io.ByteArrayInputStream;

/**
 * Service interface for generating employee reports.
 */
public interface ReportGenerator {

    /**
     * Generates a report based on provided criteria.
     *
     * @param companyId  The ID of the company to filter employees by
     * @param name       The name to filter employees by
     * @param surname    The surname to filter employees by
     * @param salaryFrom The minimum salary to filter employees by
     * @param salaryTo   The maximum salary to filter employees by
     * @return ByteArrayInputStream containing the generated report
     */
    ByteArrayInputStream generateReport(Long companyId, String name,
                                        String surname, Long salaryFrom, Long salaryTo);
}
