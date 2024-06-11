package com.example.demo.mappers;

import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.entities.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for converting Company entities to DTOs and vice versa.
 */
@Mapper(componentModel = "spring", implementationName = "companyMapper")
public interface CompanyMapper {
    String api = "http://localhost:8080/api/employee/";

    /**
     * Converts a Company entity to a CompanyDTO.
     *
     * @param company The Company entity
     * @return The corresponding CompanyDTO
     */
    @Mapping(target = "selfLink", source = "company.id", qualifiedByName = "idToLink")
    CompanyDTO toDto(Company company);

    /**
     * Converts a CompanyDTO to a Company entity.
     *
     * @param dto The CompanyDTO
     * @return The corresponding Company entity
     */
    Company toEntity(CompanyDTO dto);

    /**
     * Generates self link for a given company id.
     *
     * @param id The id of the company
     * @return The self link
     */
    @Named("idToLink")
    default String idToLink(Long id) {
        return api + id;
    }
}
