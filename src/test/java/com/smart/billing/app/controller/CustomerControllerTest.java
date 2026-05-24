/*
Generate unit tests for CustomerController using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock ICustomerService
- Use MockMvc (standalone setup)
- Test endpoints:
    - GET getAll  (success, validation errors, resource not found, internal error
    - GET getById  (success, validation errors, resource not found, internal error
    - POST create  (success, validation errors, internal error)
    - PUT update   (success, validation errors, internal error)
    - DELETE delete (success, validation errors, internal error)
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
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.billing.app.dto.CustomerRequestDTO;
import com.smart.billing.app.dto.CustomerResponseDTO;
import com.smart.billing.app.exception.GlobalExceptionHandler;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.service.ICustomerService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerControllerTest Unit Tests")
public class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper;
    private CustomerRequestDTO validRequestDTO;
    private CustomerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler()) 
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();

        validRequestDTO = new CustomerRequestDTO("123456789", "John Doe", "john.doe@example.com", "123-456-7890");

        responseDTO = new CustomerResponseDTO(
                1, "123456789", "John Doe", "john.doe@example.com", "123-456-7890",
                BigDecimal.ZERO, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void getAll_ShouldReturnListOfCustomers_WhenSuccess() throws Exception {
        // Given
        when(customerService.findAll()).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taxId").value("123456789"));

        verify(customerService, times(1)).findAll();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    void getAll_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(customerService.findAll()).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).findAll();
        verifyNoMoreInteractions(customerService);
    }


  
    @Test
    void getById_ShouldReturnCustomer_WhenSuccess() throws Exception {
        // Given
        when(customerService.findById(1)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taxId").value("123456789"));

        verify(customerService, times(1)).findById(1);
        verifyNoMoreInteractions(customerService);
    }

    

    @Test
    void getById_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customer/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    
    @Test
    void getById_ShouldReturnInternalServerError_WhenResourceNotFound() throws Exception {
        // Given
        when(customerService.findById(1)).thenThrow(new ResourceNotFoundException("Customer not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).findById(1);
        verifyNoMoreInteractions(customerService);
    }

    
    @Test
    void getById_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(customerService.findById(1)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).findById(1);
        verifyNoMoreInteractions(customerService);
    }

    

    @Test
    void create_ShouldReturnCreatedCustomer_WhenSuccess() throws Exception {
        // Given
        when(customerService.create(validRequestDTO)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taxId").value("123456789"));

        verify(customerService, times(1)).create(validRequestDTO);
        verifyNoMoreInteractions(customerService);
    }

    
    @Test
    void create_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given
        CustomerRequestDTO invalidRequestDTO = new CustomerRequestDTO("", "", "", "");

        // When & Then
        mockMvc.perform(post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    
    @Test
    void create_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    
    @Test
    void create_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    void create_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(customerService.create(validRequestDTO)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(post("/api/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).create(validRequestDTO);
        verifyNoMoreInteractions(customerService);
    }

    
    @Test
    void update_ShouldReturnUpdatedCustomer_WhenSuccess() throws Exception {
        // Given
        when(customerService.update(1, validRequestDTO)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taxId").value("123456789"));

        verify(customerService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(customerService);
    }

    
    @Test
    void update_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/customer/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    
    @Test
    void update_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given
        CustomerRequestDTO invalidRequestDTO = new CustomerRequestDTO("", "", "", "");

        // When & Then
        mockMvc.perform(put("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    void update_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    void update_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    
    @Test
    void update_ShouldReturnInternalServerError_WhenResourceNotFound() throws Exception {
        // Given
        when(customerService.update(1, validRequestDTO)).thenThrow(new ResourceNotFoundException("Customer not found"));

        // When & Then
        mockMvc.perform(put("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(customerService);
    }

    
    @Test
    void update_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(customerService.update(1, validRequestDTO)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(put("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(customerService);
    }

    
    @Test
    void delete_ShouldReturnNoContent_WhenSuccess() throws Exception {
        // Given
        doNothing().when(customerService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(1);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/customer/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    void delete_ShouldReturnInternalServerError_WhenResourceNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Customer not found")).when(customerService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).delete(1);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    void delete_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        doThrow(new RuntimeException("Internal error")).when(customerService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).delete(1);
        verifyNoMoreInteractions(customerService);
    }


}
