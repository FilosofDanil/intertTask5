package com.example.demo.dtos.company;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO class for representing a company in the response.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyResponseDTO extends CompanyDTO {
    /**
     * The self link of the company.
     */
    String selfLink;
}
