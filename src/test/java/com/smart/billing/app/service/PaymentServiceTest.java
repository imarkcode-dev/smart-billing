/*
Generate unit tests for PaymentService using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock paymentRepository
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

package com.smart.billing.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.billing.app.domain.Invoice;
import com.smart.billing.app.domain.Payment;
import com.smart.billing.app.dto.PaymentRequestDTO;
import com.smart.billing.app.dto.PaymentResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.InvoiceRepository;
import com.smart.billing.app.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Invoice invoice;
    private Payment payment;
    private PaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        invoice = Invoice.builder()
                .id(1)
                .invoiceNumber("INV-1001")
                .totalAmount(new BigDecimal("1000.00"))
                .status("PENDING")
                .build();

        payment = Payment.builder()
                .id(1)
                .invoice(invoice)
                .paymentDate(LocalDateTime.now())
                .amountPaid(new BigDecimal("1000.00"))
                .paymentMethod("CREDIT_CARD")
                .referenceNumber("REF-12345")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = new PaymentRequestDTO(
                1,
                LocalDateTime.now(),
                new BigDecimal("1000.00"),
                "CREDIT_CARD",
                "REF-12345"
        );
    }

    @Test
    void findAll_ShouldReturnListOfPaymentResponseDTO() {
        when(paymentRepository.findAllInvoiceWithInvoice()).thenReturn(List.of(payment));

        List<PaymentResponseDTO> result = paymentService.findAllInvoice();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(payment.getId(), result.get(0).id());
        verify(paymentRepository, times(1)).findAllInvoiceWithInvoice(); 
    }

    @Test
    void findById_ShouldReturnPaymentResponseDTO_WhenPaymentExists() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));

        PaymentResponseDTO result = paymentService.findById(1);

        assertNotNull(result);
        assertEquals(payment.getId(), result.id());
        verify(paymentRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenPaymentDoesNotExist() {
        when(paymentRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> paymentService.findById(1));
        assertEquals("Payment not found with id: 1", exception.getMessage());
        verify(paymentRepository, times(1)).findById(1);
    }

    @Test
    void create_ShouldReturnPaymentResponseDTO_WhenValidRequest() {
        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponseDTO result = paymentService.create(requestDTO);

        assertNotNull(result);
        assertEquals(payment.getAmountPaid(), result.amountPaid());
        verify(invoiceRepository, times(1)).findById(1);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenInvoiceDoesNotExist() {
        when(invoiceRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> paymentService.create(requestDTO));
        assertEquals("Invoice not found", exception.getMessage());
        verify(invoiceRepository, times(1)).findById(1);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
void update_ShouldReturnPaymentResponseDTO_WhenPaymentExists() {
    PaymentRequestDTO updateDTO = new PaymentRequestDTO(
            1,
            LocalDateTime.now(),
            new BigDecimal("500.00"),
            "CASH",
            "REF-678"
    );

    when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    PaymentResponseDTO result = paymentService.update(1, updateDTO);

    assertNotNull(result);
    verify(paymentRepository, times(1)).findById(1);
    verify(paymentRepository, times(1)).save(any(Payment.class));
    // invoiceRepository should NOT be called since invoiceId didn't change
    verify(invoiceRepository, never()).findById(anyInt());
}


    @Test
    void update_ShouldThrowResourceNotFoundException_WhenPaymentDoesNotExist() {
        when(paymentRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> paymentService.update(1, requestDTO));
        assertEquals("Payment not found with ID: 1", exception.getMessage());
        verify(paymentRepository, times(1)).findById(1);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void delete_ShouldDeletePayment_WhenPaymentExists() {
        when(paymentRepository.existsById(1)).thenReturn(true);

        paymentService.delete(1);

        verify(paymentRepository, times(1)).existsById(1);
        verify(paymentRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenPaymentDoesNotExist() {
        when(paymentRepository.existsById(1)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> paymentService.delete(1));
        assertEquals("Payment not found with ID: 1", exception.getMessage());
        verify(paymentRepository, times(1)).existsById(1);
        verify(paymentRepository, never()).deleteById(anyInt());
    }
}
