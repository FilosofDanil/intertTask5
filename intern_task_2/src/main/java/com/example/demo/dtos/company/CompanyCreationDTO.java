package com.example.demo.dtos.company;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO class for creating a new company.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreationDTO extends CompanyDTO {
    @NotBlank(message = "Name is mandatory")
    String name;

    /**
     * Constructor for CompanyCreationDTO.
     *
     * @param name          The name of the company
     * @param country       The country of the company
     * @param foundationDate The foundation date of the company
     */
    public CompanyCreationDTO(String name, String country, String foundationDate) {
        super(name, country, foundationDate);
        this.name = name;
    }
}
