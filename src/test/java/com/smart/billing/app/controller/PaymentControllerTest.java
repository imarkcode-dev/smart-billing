package com.smart.billing.app.controller;

import static org.mockito.ArgumentMatchers.*;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smart.billing.app.dto.PaymentRequestDTO;
import com.smart.billing.app.dto.PaymentResponseDTO;
import com.smart.billing.app.exception.GlobalExceptionHandler;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.service.IPaymentService;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentController Unit Tests")
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;
    private PaymentRequestDTO validRequestDTO;
    private PaymentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validRequestDTO = new PaymentRequestDTO(
                1,
                LocalDateTime.now(),
                new BigDecimal("1000.00"),
                "CREDIT_CARD",
                "REF-12345"
        );

        responseDTO = new PaymentResponseDTO(
        1,
        1,
        "INV-1001",
        LocalDateTime.now(),
        new BigDecimal("1000.00"),
        "CREDIT_CARD",
        "REF-12345",
        LocalDateTime.now(),
        LocalDateTime.now()
);

    }

    @Test
    void findAll_ShouldReturnListOfPayments_WhenSuccess() throws Exception {
        // Given
        when(paymentService.findAllInvoice()).thenReturn(List.of(responseDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].referenceNumber").value("REF-12345"));

        verify(paymentService, times(1)).findAllInvoice();
    }

    @Test
    void findAll_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(paymentService.findAllInvoice()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(paymentService, times(1)).findAllInvoice();
    }

    @Test
    void findById_ShouldReturnPayment_WhenExists() throws Exception {
        // Given
        when(paymentService.findById(1)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/payment/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amountPaid").value(1000.00));

        verify(paymentService, times(1)).findById(1);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenPaymentDoesNotExist() throws Exception {
        // Given
        when(paymentService.findById(1)).thenThrow(new ResourceNotFoundException("Payment not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/payment/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).findById(1);
    }

    @Test
    void findById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/payment/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    void create_ShouldReturnCreatedPayment_WhenValidRequest() throws Exception {
        when(paymentService.create(any(PaymentRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.referenceNumber").value("REF-12345"));

        verify(paymentService, times(1)).create(any(PaymentRequestDTO.class));
    }


    @Test
    void create_ShouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        // Given: Empty body
        // When & Then
        mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    void create_ShouldReturnNotFound_WhenInvoiceNotFoundInService() throws Exception {
        // Given
        when(paymentService.create(any(PaymentRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Invoice not found"));

        // When & Then
        mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).create(any(PaymentRequestDTO.class));
    }

    @Test
    void update_ShouldReturnUpdatedPayment_WhenExists() throws Exception {
        // Given
        when(paymentService.update(eq(1), any(PaymentRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/payment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(paymentService, times(1)).update(eq(1), any(PaymentRequestDTO.class));
    }

    @Test
    void update_ShouldReturnNotFound_WhenPaymentDoesNotExist() throws Exception {
        // Given
        when(paymentService.update(eq(1), any(PaymentRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Payment not found"));

        // When & Then
        mockMvc.perform(put("/api/v1/payment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).update(eq(1), any(PaymentRequestDTO.class));
    }

    @Test
    void delete_ShouldReturnNoContent_WhenSuccess() throws Exception {
        // Given
        doNothing().when(paymentService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/payment/1"))
                .andExpect(status().isNoContent());

        verify(paymentService, times(1)).delete(1);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenPaymentDoesNotExist() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Payment not found")).when(paymentService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/payment/1"))
                .andExpect(status().isNotFound());
    }
}
