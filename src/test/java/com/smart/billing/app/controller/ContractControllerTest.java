/*
Generate unit tests for ContractController using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock IContractService
- Use MockMvc (standalone setup)
- Test endpoints:
    - GET getAll  (success, validation errors, resource not found, internal error
    - GET getById  (success, validation errors, resource not found, internal error
    - POST create  (success, validation errors, internal error)
    - PUT update   (success, validation errors, internal error)
    - DELETE delete (success, validation errors, internal error)
    - GET getContractsByCustomer (success, validation errors, resource not found, internal error
    - GET getContractsByStatus (success, validation errors, resource not found, internal error
    - Follow Given-When-Then structure
- Verify service interactions (verify, verifyNoInteractions, verifyNoMoreInteractions)
- Validate HTTP status codes and response body
- Cover edge cases: null, blank, malformed JSON, empty body
- Achieve 100% line and branch coverage
- Do not use @SpringBootTest
*/

package com.smart.billing.app.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smart.billing.app.dto.ContractRequestDTO;
import com.smart.billing.app.dto.ContractResponseDTO;
import com.smart.billing.app.exception.GlobalExceptionHandler;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.service.IContractService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractControllerTest Unit Tests")
public class ContractControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IContractService contractService;

    @InjectMocks
    private ContractController contractController;

    @Autowired
    private ObjectMapper objectMapper;

    private ContractRequestDTO validRequestDTO;
    private ContractResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(contractController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        validRequestDTO = new ContractRequestDTO(
                1, "Service Contract", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31), new BigDecimal("1000.00"), "USD"
        );

        responseDTO = new ContractResponseDTO(
                1, 1, "John Doe", "Service Contract",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                new BigDecimal("1000.00"), "USD", "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // GET /api/v1/contract Tests
    @Test
    void getAll_ShouldReturnListOfContracts_WhenSuccess() throws Exception {
        // Given
        when(contractService.findAll()).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/contract")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Service Contract"));

        verify(contractService, times(1)).findAll();
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void getAll_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(contractService.findAll()).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/contract")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(contractService, times(1)).findAll();
        verifyNoMoreInteractions(contractService);
    }

    // GET /api/v1/contract/{id} Tests
    @Test
    void getContractById_ShouldReturnContract_WhenSuccess() throws Exception {
        // Given
        when(contractService.findById(1)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Service Contract"));

        verify(contractService, times(1)).findById(1);
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void getContractById_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/contract/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void getContractById_ShouldReturnNotFound_WhenResourceNotFound() throws Exception {
        // Given
        when(contractService.findById(1)).thenThrow(new ResourceNotFoundException("Contract not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(contractService, times(1)).findById(1);
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void getContractById_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(contractService.findById(1)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(contractService, times(1)).findById(1);
        verifyNoMoreInteractions(contractService);
    }
     
    // POST /api/v1/contract Tests
    @Test
    void createContract_ShouldReturnCreatedContract_WhenSuccess() throws Exception {
        // Given
        when(contractService.create(validRequestDTO)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                //.andExpect(jsonPath("$.title").value("Service Contract"));
                .andExpect(jsonPath("$.title").value(responseDTO.title() ));

        verify(contractService, times(1)).create(validRequestDTO);
        verifyNoMoreInteractions(contractService);
    }

    

    @Test
    void createContract_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given
        ContractRequestDTO invalidRequestDTO = new ContractRequestDTO(
                null, "", null, null, null, ""
        );

        // When & Then
        mockMvc.perform(post("/api/v1/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    
    @Test
    void createContract_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    
    @Test
    void createContract_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    
    @Test
    void createContract_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(contractService.create(validRequestDTO)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(post("/api/v1/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(contractService, times(1)).create(validRequestDTO);
        verifyNoMoreInteractions(contractService);
    }

    
    // PUT /api/v1/contract/{id} Tests
    @Test
    void updateContract_ShouldReturnUpdatedContract_WhenSuccess() throws Exception {
        // Given
        when(contractService.update(1, validRequestDTO)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Service Contract"));

        verify(contractService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(contractService);
    }

    
    @Test
    void updateContract_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/contract/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    
    @Test
    void updateContract_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given
        ContractRequestDTO invalidRequestDTO = new ContractRequestDTO(
                null, "", null, null, null, ""
        );

        // When & Then
        mockMvc.perform(put("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void updateContract_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void updateContract_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void updateContract_ShouldReturnNotFound_WhenResourceNotFound() throws Exception {
        // Given
        when(contractService.update(1, validRequestDTO)).thenThrow(new ResourceNotFoundException("Contract not found"));

        // When & Then
        mockMvc.perform(put("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isNotFound());

        verify(contractService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void updateContract_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(contractService.update(1, validRequestDTO)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(put("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(contractService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(contractService);
    }

    
    // DELETE /api/v1/contract/{id} Tests
    @Test
    void deleteContract_ShouldReturnNoContent_WhenSuccess() throws Exception {
        // Given
        doNothing().when(contractService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(contractService, times(1)).delete(1);
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void deleteContract_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/contract/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void deleteContract_ShouldReturnNotFound_WhenResourceNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Contract not found")).when(contractService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(contractService, times(1)).delete(1);
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void deleteContract_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        doThrow(new RuntimeException("Internal error")).when(contractService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/contract/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(contractService, times(1)).delete(1);
        verifyNoMoreInteractions(contractService);
    }

    // GET /api/v1/contract/customer/{customerId} Tests
    @Test
    void getContractsByCustomer_ShouldReturnListOfContracts_WhenSuccess() throws Exception {
        // Given
        when(contractService.findByCustomerId(1)).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/contract/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].customerName").value("John Doe"));

        verify(contractService, times(1)).findByCustomerId(1);
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void getContractsByCustomer_ShouldReturnBadRequest_WhenInvalidCustomerId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/contract/customer/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contractService);
    }

    @Test
    void getContractsByCustomer_ShouldReturnEmptyList_WhenNoContractsFound() throws Exception {
        // Given
        when(contractService.findByCustomerId(1)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/contract/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(contractService, times(1)).findByCustomerId(1);
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void getContractsByCustomer_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(contractService.findByCustomerId(1)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/contract/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(contractService, times(1)).findByCustomerId(1);
        verifyNoMoreInteractions(contractService);
    }

    // GET /api/v1/contract/status/{status} Tests
    @Test
    void getContractsByStatus_ShouldReturnListOfContracts_WhenSuccess() throws Exception {
        // Given
        when(contractService.findByStatus("ACTIVE")).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/contract/status/ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].title").value("Service Contract"));

        verify(contractService, times(1)).findByStatus("ACTIVE");
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void getContractsByStatus_ShouldReturnEmptyList_WhenNoContractsWithStatusFound() throws Exception {
        // Given
        when(contractService.findByStatus("INACTIVE")).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/contract/status/INACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(contractService, times(1)).findByStatus("INACTIVE");
        verifyNoMoreInteractions(contractService);
    }

    @Test
    void getContractsByStatus_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(contractService.findByStatus("ACTIVE")).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/contract/status/ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(contractService, times(1)).findByStatus("ACTIVE");
        verifyNoMoreInteractions(contractService);
    }
        
    

}
