package com.example.demo.controller;

import com.example.demo.dtos.company.CompanyDTO;
import com.example.demo.dtos.employee.EmployeeCreationDTO;
import com.example.demo.dtos.employee.EmployeeDTO;
import com.example.demo.exceptions.NoContentPresentException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.CRUDService;
import com.example.demo.services.EmployeeService;
import com.example.demo.services.ReportGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CRUDService<EmployeeDTO> employeeCrudService;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private ReportGenerator reportGenerator;

    private List<EmployeeDTO> employeeDTOList;

    @BeforeEach
    public void setUp() {
        employeeDTOList = new ArrayList<>();
        employeeDTOList.add(new EmployeeDTO(1L, "Danylo", "Berkovskyi", "50000", "2024-04-28", "Developer",
                new CompanyDTO("Company1", "Ukraine", "2024-04-28")));
        employeeDTOList.add(new EmployeeDTO(2L, "Jane", "Smith", "60000", "2024-04-28", "Manager",
                new CompanyDTO("Company2", "Germany", "2024-04-28")));
    }

    @Nested
    class GetTests {
        @Test
        public void getAllEmployees() throws Exception {
            when(employeeCrudService.getAll())
                    .thenReturn(employeeDTOList);

            mockMvc.perform(get("/api/employee"))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(0).getName())))
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(0).getSurname())))
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(0).getHiringDate())))
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(0).getCompany().getName())));

        }

        @Test
        public void getEmployeeById() throws Exception {
            when(employeeCrudService.getById(1L))
                    .thenReturn(employeeDTOList.get(1));

            mockMvc.perform(get("/api/employee/1"))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(1).getName())))
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(1).getSurname())))
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(1).getHiringDate())))
                    .andExpect(content()
                            .string(containsString(employeeDTOList.get(1).getCompany().getName())));
        }

        @Test
        public void getEmployeeByIdNotFound() throws Exception {
            String message = "Cannot find employee with id: 3";
            when(employeeCrudService.getById(3L))
                    .thenThrow(new ResourceNotFoundException(message));
            mockMvc.perform(get("/api/employee/3"))
                    .andExpect(status().isNotFound())
                    .andExpect(content()
                            .string(containsString(message)));
        }
    }

    @Nested
    class CreateTests {


        @Test
        public void createEmployee() throws Exception {
            EmployeeCreationDTO newEmployee =
                    new EmployeeCreationDTO(1L,"Danylo", "Berkovskyi", "50000", "2024-04-28", "Developer", new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
            EmployeeDTO returnedValue = new EmployeeDTO(1L,"Danylo", "Berkovskyi", "50000", "2024-04-28", "Developer", new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
            when(employeeCrudService.create(any(EmployeeCreationDTO.class)))
                    .thenReturn(returnedValue);
            String newContentJson = objectMapper.writeValueAsString(newEmployee);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isCreated())
                    .andExpect(content()
                            .string(containsString(returnedValue.getName())))
                    .andExpect(content()
                            .string(containsString(returnedValue.getSurname())))
                    .andExpect(content()
                            .string(containsString(returnedValue.getHiringDate())))
                    .andExpect(content()
                            .string(containsString(returnedValue.getCompany().getName())));
        }

        @Test
        public void createEmployeeInvalidName() throws Exception {
            EmployeeCreationDTO newEmployee =
                    new EmployeeCreationDTO(1L, "", "Berkovskyi", "50000", "2024-04-28", "Developer", new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
            String newContentJson = objectMapper.writeValueAsString(newEmployee);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .string(containsString("Validation failed\",\"cause\":[\"name - Name is mandatory\"")));
        }

        @Test
        public void createEmployeeInvalidSurname() throws Exception {
            EmployeeCreationDTO newEmployee =
                    new EmployeeCreationDTO(1L,"Danylo", "", "50000", "2024-04-28", "Developer", new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
            String newContentJson = objectMapper.writeValueAsString(newEmployee);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .string(containsString("Validation failed\",\"cause\":[\"surname - Surname is mandatory\"")));
        }

        @Test
        public void createEmployeeInvalidSalary() throws Exception {
            EmployeeCreationDTO newEmployee =
                    new EmployeeCreationDTO(1L, "Danylo", "Berkovskyi", "-1000", "2024-04-28", "Developer", new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
            String newContentJson = objectMapper.writeValueAsString(newEmployee);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .string(containsString("\"Validation failed\",\"cause\":[\"salary - Salary may not be less than zero\"]")));
        }

        @Test
        public void createEmployeeInvalidCompany() throws Exception {
            EmployeeCreationDTO newEmployee =
                    new EmployeeCreationDTO(1L,"Danylo", "Berkovskyi", "50000", "2024-04-28", "Developer", null);
            String newContentJson = objectMapper.writeValueAsString(newEmployee);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .string(containsString("\"Validation failed\",\"cause\":[\"company - Company may not be null\"")));
        }

    }

    @Nested
    class UpdateTests {

        @Test
        public void updateEmployee() throws Exception {
            EmployeeDTO updatedEmployee =
                    new EmployeeDTO(1L,"Danylo", "Berkovskyi", "50000", "2024-04-28", "Developer", new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
            String newContentJson = objectMapper.writeValueAsString(updatedEmployee);
            mockMvc.perform(put("/api/employee/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isOk());
        }

        @Test
        public void updateEmployeeNotFound() throws Exception {
            String message = "Cannot find employee with id: 3";
            EmployeeDTO updatedEmployee =
                    new EmployeeDTO(1L,"Danylo", "Berkovskyi", "50000", "2024-04-28", "Developer", new CompanyDTO("Company1", "Ukraine", "2024-04-28"));
            String newContentJson = objectMapper.writeValueAsString(updatedEmployee);
            doAnswer((Answer<Void>) invocation -> {
                throw new ResourceNotFoundException(message);
            }).when(employeeCrudService)
                    .update(any(EmployeeDTO.class), eq(3L));
            mockMvc.perform(put("/api/employee/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newContentJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content()
                            .string(containsString(message)));
        }

    }

    @Nested
    class DeleteTests {
        @Test
        public void deleteEmployee() throws Exception {
            mockMvc.perform(delete("/api/employee/1"))
                    .andExpect(status().isOk());
        }

        @Test
        public void deleteEmployeeWithNoContent() throws Exception {
            String message = "No content present with id: 3";
            doAnswer((Answer<Void>) invocation -> {
                throw new NoContentPresentException(message);
            }).when(employeeCrudService)
                    .delete(eq(3L));
            mockMvc.perform(delete("/api/employee/3"))
                    .andExpect(status().isNoContent())
                    .andExpect(content()
                            .string(containsString(message)));
        }

    }

    @Nested
    class ReportTests {

        @Test
        public void getAllEmployeesWithPagination() throws Exception {
            int page = 0;
            int size = 5;
            Pageable pageable = PageRequest.of(page, size);
            Page<EmployeeDTO> employeeDTOPage = new PageImpl<>(employeeDTOList);
            when(employeeService.getAllEmployeesWithPagination(eq(1L), eq("Danylo"), eq("Berkovskyi"), eq(40000L), eq(70000L), eq(pageable)))
                    .thenReturn(employeeDTOPage);
            mockMvc.perform(post("/api/employee/_list")
                            .param("page", Integer.toString(page))
                            .param("size", Integer.toString(size)).param("companyId", "1")
                            .param("name", "Danylo")
                            .param("surname", "Berkovskyi")
                            .param("salaryFrom", "40000")
                            .param("salaryTo", "70000"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        public void generateReport() throws Exception {
            ByteArrayInputStream inputStream = new ByteArrayInputStream("Test report content".getBytes());
            when(reportGenerator.generateReport(eq(1L), eq("Danylo"), eq("Berkovskyi"), eq(40000L), eq(70000L)))
                    .thenReturn(inputStream);

            mockMvc.perform(post("/api/employee/_report")
                            .param("companyId", "1")
                            .param("name", "Danylo")
                            .param("surname", "Berkovskyi")
                            .param("salaryFrom", "40000")
                            .param("salaryTo", "70000"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employees_report.csv"))
                    .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
        }

        @Test
        public void generateReportInvalidSalary() throws Exception {
            ByteArrayInputStream inputStream = new ByteArrayInputStream("Test report content".getBytes());
            when(reportGenerator.generateReport(eq(1L), eq("Danylo"), eq("Berkovskyi"), eq(40000L), eq(70000L)))
                    .thenReturn(inputStream);

            mockMvc.perform(post("/api/employee/_report")
                            .param("companyId", "1")
                            .param("name", "Danylo")
                            .param("surname", "Berkovskyi")
                            .param("salaryFrom", "-40000")
                            .param("salaryTo", "70000"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void generateReportInvalidCompany() throws Exception {
            ByteArrayInputStream inputStream = new ByteArrayInputStream("Test report content".getBytes());
            when(reportGenerator.generateReport(eq(1L), eq("Danylo"), eq("Berkovskyi"), eq(40000L), eq(70000L)))
                    .thenReturn(inputStream);

            mockMvc.perform(post("/api/employee/_report")
                            .param("companyId", "300")
                            .param("name", "Danylo")
                            .param("surname", "Berkovskyi")
                            .param("salaryFrom", "40000")
                            .param("salaryTo", "70000"))
                    .andExpect(status().isBadRequest());
        }
    }

}