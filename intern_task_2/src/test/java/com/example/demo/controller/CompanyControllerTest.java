package com.example.demo.controller;

import com.example.demo.dtos.company.CompanyCreationDTO;
import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.exceptions.NoContentPresentException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.CRUDService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CRUDService<CompanyDTO> companyCrudService;

    private List<CompanyDTO> companyDTOList;

    @BeforeEach
    void setUp() {


        companyDTOList = new ArrayList<>();
        companyDTOList.add(new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
        companyDTOList.add(new CompanyDTO("Company2", "Germany", "2024-04-28"));
    }

    @Nested
    class GetTests {

        @Test
        void getAllCompanies() throws Exception {
            when(companyCrudService.getAll()).thenReturn(companyDTOList);

            mockMvc.perform(get("/api/company"))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(companyDTOList.get(0).getName())))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(companyDTOList.get(0).getCountry())));
        }

        @Test
        void getCompanyById() throws Exception {
            when(companyCrudService.getById(1L))
                    .thenReturn(companyDTOList.get(1));

            mockMvc.perform(get("/api/company/1"))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(companyDTOList.get(1).getName())))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(companyDTOList.get(1).getCountry())));
        }

        @Test
        void getCompanyByIdNotFound() throws Exception {
            String message = "Cannot find company with id: 3";
            when(companyCrudService.getById(3L))
                    .thenThrow(new ResourceNotFoundException(message));
            mockMvc.perform(get("/api/company/3"))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(message)));
        }
    }

    @Nested
    class CreateTests {
        @Test
        void createCompany() throws Exception {
            CompanyCreationDTO newCompany
                    = new CompanyCreationDTO("Company3", "USA", "2024-04-28");
            CompanyDTO returnedValue = new CompanyDTO("Company3", "USA", "2024-04-28");
            when(companyCrudService.create(any(CompanyCreationDTO.class)))
                    .thenReturn(returnedValue);
            String newContentJson = objectMapper.writeValueAsString(newCompany);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(returnedValue.getName())))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(returnedValue.getCountry())));
        }

        @Test
        void createCompanyInvalidName() throws Exception {
            CompanyCreationDTO newCompany
                    = new CompanyCreationDTO("", "USA", "2024-04-28");
            String newContentJson = objectMapper.writeValueAsString(newCompany);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("Validation failed\",\"cause\":[\"name - Name is mandatory\"")));
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void updateCompany() throws Exception {
            CompanyDTO updatedCompany = new CompanyDTO("Company3", "USA", "2024-04-28");
            String newContentJson = objectMapper.writeValueAsString(updatedCompany);
            mockMvc.perform(put("/api/company/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isOk());
        }

        @Test
        void updateCompanyNotFound() throws Exception {
            String message = "Cannot find company with id: 3";
            CompanyDTO updatedCompany = new CompanyDTO("Company3", "USA", "2024-04-28");
            String newContentJson = objectMapper.writeValueAsString(updatedCompany);
            doAnswer((Answer<Void>) invocation -> {
                throw new ResourceNotFoundException(message);
            }).when(companyCrudService)
                    .update(any(CompanyDTO.class), eq(3L));
            mockMvc.perform(put("/api/company/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(message)));
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void deleteCompany() throws Exception {
            mockMvc.perform(delete("/api/company/1"))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteCompanyWithNoContent() throws Exception {
            String message = "No content present with id: 3";
            doAnswer((Answer<Void>) invocation -> {
                throw new NoContentPresentException(message);
            }).when(companyCrudService)
                    .delete(eq(3L));
            mockMvc.perform(delete("/api/company/3"))
                    .andExpect(status().isNoContent())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString(message)));
        }
    }
}