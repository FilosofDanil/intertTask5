package com.example.demo.dtos.company;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO class for representing company information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyDTO {
    String name;
    String country;
    String foundationDate;
}
