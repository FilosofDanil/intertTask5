package com.example.demo.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO class for representing upload statistics.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Statistic {
    int successfullyWrittenRows;
    int unsuccessfullyWrittenRows;
}
