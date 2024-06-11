package com.example.demo.repositories;

import com.example.demo.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Employee entities.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Updates the employee with the given id.
     *
     * @param id          The id of the employee to update
     * @param name        The new name for the employee (nullable)
     * @param surname     The new surname for the employee (nullable)
     * @param salary      The new salary for the employee (nullable)
     * @param hiringDate  The new hiring date for the employee (nullable)
     * @param job         The new job for the employee (nullable)
     * @param companyName The new company name for the employee (nullable)
     */
    @Modifying
    @Query(value = "UPDATE employees SET " +
            "employee_name = CASE WHEN :name IS NOT NULL THEN :name ELSE employee_name END, " +
            "employee_surname = CASE WHEN :surname IS NOT NULL THEN :surname ELSE employee_surname END, " +
            "salary = CASE WHEN :salary IS NOT NULL THEN :salary ELSE salary END, " +
            "hiring_date = CASE WHEN :hiringDate IS NOT NULL THEN CAST(:hiringDate AS timestamp) ELSE hiring_date END, " +
            "job = CASE WHEN :job IS NOT NULL THEN :job ELSE job END, " +
            "company_id = CASE WHEN :companyName IS NOT NULL " +
            "THEN (SELECT id FROM companies WHERE company_name = :companyName) ELSE company_id END " +
            "WHERE id = :id",
            nativeQuery = true)
    void updateEmployee(@Param("id") Long id,
                        @Param("name") String name,
                        @Param("surname") String surname,
                        @Param("salary") Integer salary,
                        @Param("hiringDate") String hiringDate,
                        @Param("job") String job,
                        @Param("companyName") String companyName);

    /**
     * Inserts a new employee into the database.
     *
     * @param name        The name of the employee
     * @param surname     The surname of the employee
     * @param salary      The salary of the employee
     * @param hiringDate  The hiring date of the employee
     * @param job         The job of the employee
     * @param companyName The name of the company the employee works for
     * @return The inserted Employee
     */
    @Query(value = "WITH inserted_employee AS (" +
            "    INSERT INTO employees (id, employee_name, employee_surname, salary, hiring_date, job, company_id)" +
            "    VALUES" +
            "        (nextval('employee_seq'), :name, :surname, :salary, :hiringDate, :job, " +
            "(SELECT id FROM companies WHERE company_name = :companyName))" +
            "    RETURNING *" +
            ")" +
            "SELECT * FROM inserted_employee;", nativeQuery = true)
    Employee insertEmployee(@Param("name") String name,
                            @Param("surname") String surname,
                            @Param("salary") Integer salary,
                            @Param("hiringDate") LocalDate hiringDate,
                            @Param("job") String job,
                            @Param("companyName") String companyName);

    /**
     * Retrieves a page of employees with optional filtering.
     *
     * @param companyId  The id of the company to filter by (nullable)
     * @param name       The name of the employee to filter by (nullable)
     * @param surname    The surname of the employee to filter by (nullable)
     * @param salaryFrom The minimum salary to filter by (nullable)
     * @param salaryTo   The maximum salary to filter by (nullable)
     * @param pageable   The pagination information
     * @return A page of Employee entities
     */
    @Query("SELECT e FROM Employee e " +
            "WHERE (:companyId IS NULL OR e.company.id = :companyId) " +
            "AND (:name IS NULL OR e.name = :name) " +
            "AND (:surname IS NULL OR e.surname = :surname) " +
            "AND (:salaryFrom IS NULL OR e.salary >= :salaryFrom) " +
            "AND (:salaryTo IS NULL OR e.salary <= :salaryTo)")
    Page<Employee> getAllEmployeePages(
            @Param("companyId") Long companyId,
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("salaryFrom") Long salaryFrom,
            @Param("salaryTo") Long salaryTo,
            Pageable pageable
    );

    /**
     * Retrieves a list of employees with optional filtering.
     *
     * @param companyId  The id of the company to filter by (nullable)
     * @param name       The name of the employee to filter by (nullable)
     * @param surname    The surname of the employee to filter by (nullable)
     * @param salaryFrom The minimum salary to filter by (nullable)
     * @param salaryTo   The maximum salary to filter by (nullable)
     * @return A list of Employee entities
     */
    @Query("SELECT e FROM Employee e " +
            "WHERE (:companyId IS NULL OR e.company.id = :companyId) " +
            "AND (:name IS NULL OR e.name = :name) " +
            "AND (:surname IS NULL OR e.surname = :surname) " +
            "AND (:salaryFrom IS NULL OR e.salary >= :salaryFrom) " +
            "AND (:salaryTo IS NULL OR e.salary <= :salaryTo)")
    List<Employee> getAllEmployeeWithFilters(
            @Param("companyId") Long companyId,
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("salaryFrom") Long salaryFrom,
            @Param("salaryTo") Long salaryTo
    );
}
