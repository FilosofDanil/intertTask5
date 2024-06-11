package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity class representing a company.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", sequenceName = "company_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "company_name", unique = true, nullable = false)
    String name;

    @Column(name = "country")
    String country;

    @Column(name = "foundation_date")
    LocalDate foundationDate;

    @OneToMany(mappedBy = "company", orphanRemoval = true)
    List<Employee> employees;
}
