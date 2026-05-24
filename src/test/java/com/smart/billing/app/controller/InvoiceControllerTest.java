/*
Generate unit tests for InvoiceController using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock InvoiceRepository
- Test all methods:
    - findAll
    - findById
    - create
    - update
    - delete
- Use given-when-then structure
- Verify repository interactions
- Use assertThrows for exceptions
- Achieve 100% coverage
- Do not use SpringBootTest
*/

package com.smart.billing.app.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smart.billing.app.dto.InvoiceRequestDTO;
import com.smart.billing.app.dto.InvoiceResponseDTO;
import com.smart.billing.app.exception.GlobalExceptionHandler;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.service.IInvoiceService;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceControllerTest Unit Tests")
public class InvoiceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IInvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    @Autowired
    private ObjectMapper objectMapper;
    
    private InvoiceRequestDTO validRequestDTO;
    private InvoiceResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(invoiceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        validRequestDTO = new InvoiceRequestDTO(
                1,
                "INV-1001",
                LocalDateTime.of(2024, 2, 1,0,0,0),
                LocalDateTime.of(2024, 2, 28,0,0,0),
                new BigDecimal("1000.00"),
                BigDecimal.ZERO,
                "PENDING"
        );

        responseDTO = new InvoiceResponseDTO(
                1,
                1,
                "INV-1001",
                "Service Contract",
                LocalDateTime.of(2024, 2, 1, 0,0,0),
                LocalDateTime.of(2024, 2, 28,0,0,0),
                new BigDecimal("1000.00"),
                BigDecimal.ZERO,
                "PENDING"
        );
    }

    @Test
    void getAll_ShouldReturnListOfInvoices_WhenSuccess() throws Exception {
        // Given
        when(invoiceService.findAll()).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].contractId").value(1))
                .andExpect(jsonPath("$[0].invoiceNumber").value("INV-1001"));


        verify(invoiceService, times(1)).findAll();
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void getAll_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(invoiceService.findAll()).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(invoiceService, times(1)).findAll();
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void getById_ShouldReturnInvoice_WhenSuccess() throws Exception {
        // Given
        when(invoiceService.findById(1)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/invoice/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contractId").value(1))
                .andExpect(jsonPath("$.invoiceNumber").value("INV-1001"));

        verify(invoiceService, times(1)).findById(1);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void getById_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/invoice/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void getById_ShouldReturnNotFound_WhenResourceNotFound() throws Exception {
        // Given
        when(invoiceService.findById(1)).thenThrow(new ResourceNotFoundException("Invoice not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/invoice/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(invoiceService, times(1)).findById(1);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void getById_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(invoiceService.findById(1)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/v1/invoice/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(invoiceService, times(1)).findById(1);
        verifyNoMoreInteractions(invoiceService);
    }

    
    @Test
    void create_ShouldReturnCreatedInvoice_WhenSuccess() throws Exception {
        // Given
        when(invoiceService.create(validRequestDTO)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contractId").value(1))
                .andExpect(jsonPath("$.invoiceNumber").value("INV-1001"));

        verify(invoiceService, times(1)).create(validRequestDTO);
        verifyNoMoreInteractions(invoiceService);
    }
    

   @Test
    void create_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given
        InvoiceRequestDTO invalidRequestDTO = new InvoiceRequestDTO(
                null,
                "",
                null,
                null,
                null,
                null,
                null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void create_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void create_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(invoiceService.create(validRequestDTO)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(post("/api/v1/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(invoiceService, times(1)).create(validRequestDTO);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void update_ShouldReturnUpdatedInvoice_WhenSuccess() throws Exception {
        // Given
        when(invoiceService.update(1, validRequestDTO)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contractId").value(1))
                .andExpect(jsonPath("$.invoiceNumber").value("INV-1001"));

        verify(invoiceService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void update_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/invoice/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void update_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given
        InvoiceRequestDTO invalidRequestDTO = new InvoiceRequestDTO(
                null,
                "",
                null,
                null,
                null,
                null,
                null
        );

        // When & Then
        mockMvc.perform(put("/api/v1/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void update_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void update_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void update_ShouldReturnNotFound_WhenResourceNotFound() throws Exception {
        // Given
        when(invoiceService.update(1, validRequestDTO)).thenThrow(new ResourceNotFoundException("Invoice not found"));

        // When & Then
        mockMvc.perform(put("/api/v1/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isNotFound());

        verify(invoiceService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void update_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(invoiceService.update(1, validRequestDTO)).thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(put("/api/v1/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(invoiceService, times(1)).update(1, validRequestDTO);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void delete_ShouldReturnNoContent_WhenSuccess() throws Exception {
        // Given
        doNothing().when(invoiceService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/invoice/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(invoiceService, times(1)).delete(1);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/invoice/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenResourceNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Invoice not found")).when(invoiceService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/invoice/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(invoiceService, times(1)).delete(1);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void delete_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        doThrow(new RuntimeException("Internal error")).when(invoiceService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/invoice/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(invoiceService, times(1)).delete(1);
        verifyNoMoreInteractions(invoiceService);
    }
}
