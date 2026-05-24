/*
Generate unit tests for InvoiceService using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock InvoiceRepository
- Mock ContractRepository
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
import java.time.LocalDate;
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

import com.smart.billing.app.domain.Contract;
import com.smart.billing.app.domain.Customer;
import com.smart.billing.app.domain.Invoice;
import com.smart.billing.app.dto.InvoiceRequestDTO;
import com.smart.billing.app.dto.InvoiceResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.ContractRepository;
import com.smart.billing.app.repository.InvoiceRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceServiceTest Unit Tests")
public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Customer customer;
    private Contract contract;
    private Invoice invoice;
    private InvoiceRequestDTO requestDTO;
    private InvoiceResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1)
                .taxId("123456789")
                .nameCustomer("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .build();

        contract = Contract.builder()
                .id(1)
                .customer(customer)
                .title("Service Contract")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .monthlyFee(new BigDecimal("1000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        invoice = Invoice.builder()
                .id(1)
                .contract(contract)
                .invoiceNumber("INV-1001")
                .issueDate(LocalDateTime.of(2024, 2, 1, 1, 0, 0))
                .dueDate(LocalDateTime.of(2024, 2, 10, 0, 0, 0))
                .totalAmount(contract.getMonthlyFee())
                .penaltyAmount(BigDecimal.ZERO)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = new InvoiceRequestDTO(
                1, 
                "INV-1001", 
                LocalDateTime.of(2024, 2, 1, 10, 0, 0),
                LocalDateTime.of(2024, 2, 28, 23, 0, 0),
                new BigDecimal("1000.00"),
                BigDecimal.ZERO,
                "PENDING"
        );

        responseDTO = new InvoiceResponseDTO(
                1, 
                1,
                "INV-1001",  
                "Service Contract",
                LocalDateTime.of(2024, 2, 1, 10, 0, 0),
                LocalDateTime.of(2024, 2, 28, 23, 0, 0),
                new BigDecimal("1000.00"), 
                BigDecimal.ZERO, 
                "PENDING"
        );
    }

    @Test
    void findAll_ShouldReturnListOfInvoiceResponseDTO() {
        // Given
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        // When
        List<InvoiceResponseDTO> result = invoiceService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO.id(), result.get(0).id());
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnInvoiceResponseDTO_WhenInvoiceExists() {
        // Given
        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));

        // When
        InvoiceResponseDTO result = invoiceService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO.id(), result.id());
        assertEquals(responseDTO.invoiceNumber(), result.invoiceNumber());
        verify(invoiceRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenInvoiceDoesNotExist() {
        // Given
        when(invoiceRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.findById(1));
        assertEquals("Invoice not found with id: 1", exception.getMessage());
        verify(invoiceRepository, times(1)).findById(1);
    }

    @Test
    void create_ShouldReturnInvoiceResponseDTO_WhenValidRequest() {
        // Given
        when(contractRepository.findById(1)).thenReturn(Optional.of(contract));
        when(invoiceRepository.existsByInvoiceNumber("INV-1001")).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        // When
        InvoiceResponseDTO result = invoiceService.create(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO.invoiceNumber(), result.invoiceNumber());
        assertEquals(responseDTO.totalAmount(), result.totalAmount());
        assertEquals("PENDING", result.status());
        verify(contractRepository, times(1)).findById(1);
        verify(invoiceRepository, times(1)).existsByInvoiceNumber("INV-1001");
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenContractDoesNotExist() {
        // Given
        when(contractRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.create(requestDTO));
        assertEquals("Contract not found with ID: 1", exception.getMessage());
        verify(contractRepository, times(1)).findById(1);
        verify(invoiceRepository, never()).existsByInvoiceNumber(anyString());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void create_ShouldThrowRuntimeException_WhenInvoiceNumberAlreadyExists() {
        // Given
        when(contractRepository.findById(1)).thenReturn(Optional.of(contract));
        when(invoiceRepository.existsByInvoiceNumber("INV-1001")).thenReturn(true);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.create(requestDTO));
        assertEquals("Invoice number INV-1001 already exists.", exception.getMessage());
        verify(contractRepository, times(1)).findById(1);
        verify(invoiceRepository, times(1)).existsByInvoiceNumber("INV-1001");
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void update_ShouldReturnInvoiceResponseDTO_WhenInvoiceExistsAndInvoiceNumberUnchanged() {
        // Given
        InvoiceRequestDTO updateDTO = new InvoiceRequestDTO(
                1, 
                "INV-1001", 
                LocalDateTime.of(2024, 2, 1, 0, 0, 0),
                LocalDateTime.of(2024, 3, 21, 0, 0, 0),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                "PENDING"
        );
        Invoice updatedInvoice = Invoice.builder()
                .id(1)
                .contract(contract)
                .invoiceNumber("INV-1001")
                .issueDate(LocalDateTime.of(2024, 2, 1, 0, 0, 0))
                .dueDate(LocalDateTime.of(2024, 3, 21, 0, 0, 0))
                .totalAmount(BigDecimal.valueOf(100))
                .penaltyAmount(BigDecimal.valueOf(50))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
        when(contractRepository.findById(1)).thenReturn(Optional.of(contract));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(updatedInvoice);

        // When
        InvoiceResponseDTO result = invoiceService.update(1, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(LocalDateTime.of(2024, 3, 21, 0, 0, 0), result.dueDate());
        assertEquals(BigDecimal.valueOf(50), result.penaltyAmount());
        verify(invoiceRepository, times(1)).findById(1);
        verify(contractRepository, times(1)).findById(1);
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void update_ShouldReturnInvoiceResponseDTO_WhenInvoiceNumberChangedAndUnique() {
        // Given
        InvoiceRequestDTO updateDTO = new InvoiceRequestDTO(
                1, 
                "INV-1002", 
                LocalDateTime.of(2024, 2, 1, 1, 0, 0),
                LocalDateTime.of(2024, 2, 10, 0, 0, 0),
                BigDecimal.valueOf(60),
                BigDecimal.valueOf(25),
                "PENDING"
        );
        Invoice updatedInvoice = Invoice.builder()
                .id(1)
                .contract(contract)
                .invoiceNumber("INV-1002")
                .issueDate(LocalDateTime.of(2024, 2, 1, 1, 0, 0))
                .dueDate(LocalDateTime.of(2024, 2, 10, 0, 0, 0))
                .totalAmount(BigDecimal.valueOf(60))
                .penaltyAmount(BigDecimal.valueOf(25))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
        when(contractRepository.findById(1)).thenReturn(Optional.of(contract));
        when(invoiceRepository.existsByInvoiceNumber("INV-1002")).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(updatedInvoice);

        // When
        InvoiceResponseDTO result = invoiceService.update(1, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("INV-1002", result.invoiceNumber());
        assertEquals(BigDecimal.valueOf(25), result.penaltyAmount());
        verify(invoiceRepository, times(1)).findById(1);
        verify(contractRepository, times(1)).findById(1);
        verify(invoiceRepository, times(1)).existsByInvoiceNumber("INV-1002");
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenInvoiceDoesNotExist() {
        // Given
        when(invoiceRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.update(1, requestDTO));
        assertEquals("Invoice not found with ID: 1", exception.getMessage());
        verify(invoiceRepository, times(1)).findById(1);
        verify(contractRepository, never()).findById(anyInt());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenNewContractDoesNotExist() {
        // Given
        InvoiceRequestDTO updateDTO = new InvoiceRequestDTO(
                2, 
                "INV-1001", 
                LocalDateTime.of(2024, 2, 1, 10, 0, 0),
                LocalDateTime.of(2024, 2, 28, 23, 59, 0),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "PENDING"
        );
        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
        when(contractRepository.findById(2)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.update(1, updateDTO));
        assertEquals("Contract not found", exception.getMessage());
        verify(invoiceRepository, times(1)).findById(1);
        verify(contractRepository, times(1)).findById(2);
        verify(invoiceRepository, never()).existsByInvoiceNumber(anyString());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void update_ShouldThrowRuntimeException_WhenInvoiceNumberAlreadyInUse() {
        // Given
        InvoiceRequestDTO updateDTO = new InvoiceRequestDTO(
                1, 
                "INV-1002", 
                LocalDateTime.of(2024, 2, 1, 1, 0, 0),
                LocalDateTime.of(2024, 2, 10, 0, 0, 0),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "PENDING"
        );
        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
        when(contractRepository.findById(1)).thenReturn(Optional.of(contract));
        when(invoiceRepository.existsByInvoiceNumber("INV-1002")).thenReturn(true);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.update(1, updateDTO));
        assertEquals("Invoice number INV-1002 is already in use.", exception.getMessage());
        verify(invoiceRepository, times(1)).findById(1);
        verify(contractRepository, times(1)).findById(1);
        verify(invoiceRepository, times(1)).existsByInvoiceNumber("INV-1002");
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void delete_ShouldDeleteInvoice_WhenInvoiceExists() {
        // Given
        when(invoiceRepository.existsById(1)).thenReturn(true);

        // When
        invoiceService.delete(1);

        // Then
        verify(invoiceRepository, times(1)).existsById(1);
        verify(invoiceRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenInvoiceDoesNotExist() {
        // Given
        when(invoiceRepository.existsById(1)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.delete(1));
        assertEquals("Invoice not found with ID: 1", exception.getMessage());
        verify(invoiceRepository, times(1)).existsById(1);
        verify(invoiceRepository, never()).deleteById(anyInt());
    }
}

