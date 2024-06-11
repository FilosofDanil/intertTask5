package com.example.demo.repositories;

import com.example.demo.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for Company entities.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Retrieves a company by its name.
     *
     * @param name The name of the company
     * @return An Optional containing the company, or empty if none found
     */
    Optional<Company> findByName(String name);

    /**
     * Checks if a company exists by its name.
     *
     * @param name The name of the company
     * @return true if a company with the given name exists, otherwise false
     */
    boolean existsByName(String name);

    /**
     * Updates the company with the given id.
     *
     * @param id            The id of the company to update
     * @param name          The new name for the company (nullable)
     * @param country       The new country for the company (nullable)
     * @param foundationDate The new foundation date for the company (nullable)
     */
    @Modifying
    @Query(value = "UPDATE companies SET company_name = CASE WHEN :name IS NOT NULL THEN :name ELSE company_name END," +
            "    country = CASE WHEN :country IS NOT NULL THEN :country ELSE country END," +
            "    foundation_date = CASE WHEN :foundationDate IS NOT NULL THEN :foundationDate ELSE foundation_date END " +
            "WHERE id = :id",
            nativeQuery = true)
    void updateCompany(@Param("id") Long id,
                       @Param("name") String name,
                       @Param("country") String country,
                       @Param("foundationDate") LocalDate foundationDate);
}
