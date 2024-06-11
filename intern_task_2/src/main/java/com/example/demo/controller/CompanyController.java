package com.example.demo.controller;

import com.example.demo.constants.EmailConstants;
import com.example.demo.dtos.company.CompanyCreationDTO;
import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.message.EmailMessage;
import com.example.demo.services.CRUDService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling Company related requests.
 */
@RestController
@RequestMapping("api/company")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyController {
    @Value("${kafka.topic.emailReceived}")
    String emailReceivedTopic;
    final CRUDService<CompanyDTO> companyCrudService;
    final KafkaOperations<String, EmailMessage> kafkaOperations;

    /**
     * Retrieves all companies
     * @return ResponseEntity with a list of CompanyDTOs in the body
     */
    @GetMapping("")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyCrudService.getAll());
    }

    /**
     * Retrieves a company by its ID.
     *
     * @param id The ID of the company to retrieve
     * @return ResponseEntity with the retrieved CompanyDTO in the body
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(companyCrudService.getById(id));
    }

    /**
     * Creates a new company.
     *
     * @param companyDTO The CompanyCreationDTO containing company information
     * @return ResponseEntity with the created CompanyDTO in the body
     */
    @PostMapping("")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyCreationDTO companyDTO) {
        CompanyDTO saved = companyCrudService.create(companyDTO);
        EmailMessage emailMessage = EmailMessage
                .builder()
                .subject(EmailConstants.companySubject)
                .content(EmailConstants.companyText + companyDTO.getName())
                .receivers(EmailConstants.mails)
                .sent(false)
                .build();
        kafkaOperations.send(emailReceivedTopic, "saved", emailMessage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    /**
     * Updates an existing company.
     *
     * @param id         The ID of the company to update
     * @param companyDTO The updated CompanyDTO
     * @return ResponseEntity indicating the success of the update operation
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCompany(@PathVariable("id") Long id,
                                              @RequestBody CompanyDTO companyDTO) {
        companyCrudService.update(companyDTO, id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    /**
     * Deletes a company by its ID.
     *
     * @param id The ID of the company to delete
     * @return ResponseEntity indicating the success of the delete operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
        companyCrudService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
