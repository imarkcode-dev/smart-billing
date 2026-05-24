/*
Generate unit tests for ContractService using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock ContractRepository
- Test all methods:
    - findAll
    - findById
    - create
    - update
    - delete
    - findByCustomerId
    - findByStatus
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
import com.smart.billing.app.dto.ContractRequestDTO;
import com.smart.billing.app.dto.ContractResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.ContractRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractServiceTest Unit Tests")
public class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractService contractService;

    private Contract contract;
    private ContractRequestDTO requestDTO;
    private ContractResponseDTO responseDTO;
    private Customer customer;

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

        requestDTO = new ContractRequestDTO(
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

    @Test
    void findAll_ShouldReturnListOfContractResponseDTO() {
        // Given
        when(contractRepository.findAll()).thenReturn(List.of(contract));

        // When
        List<ContractResponseDTO> result = contractService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO.id(), result.get(0).id());
        verify(contractRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoContractsExist() {
        // Given
        when(contractRepository.findAll()).thenReturn(List.of());

        // When
        List<ContractResponseDTO> result = contractService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(contractRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnContractResponseDTO_WhenContractExists() {
        // Given
        when(contractRepository.findById(1)).thenReturn(Optional.of(contract));

        // When
        ContractResponseDTO result = contractService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO.id(), result.id());
        verify(contractRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenContractDoesNotExist() {
        // Given
        when(contractRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.findById(1));
        assertEquals("Contract not found with id: 1", exception.getMessage());
        verify(contractRepository, times(1)).findById(1);
    }

    @Test
    void create_ShouldReturnContractResponseDTO_WhenValidRequest() {
        // Given
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);

        // When
        ContractResponseDTO result = contractService.create(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO.id(), result.id());
        assertEquals(responseDTO.title(), result.title());
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void create_ShouldSetDefaultValuesForCurrencyAndStatus() {
        // Given
        ContractRequestDTO dtoWithoutCurrency = new ContractRequestDTO(
                1, "Service Contract", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31), new BigDecimal("1000.00"), null
        );
        Contract contractWithDefaults = Contract.builder()
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

        when(contractRepository.save(any(Contract.class))).thenReturn(contractWithDefaults);

        // When
        ContractResponseDTO result = contractService.create(dtoWithoutCurrency);

        // Then
        assertNotNull(result);
        assertEquals("USD", result.currency());
        assertEquals("ACTIVE", result.status());
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenRequestDTOIsNull() {
        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.create(null));
        assertEquals("ContractRequestDTO cannot be null", exception.getMessage());
        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    void update_ShouldReturnContractResponseDTO_WhenContractExists() {
        // Given
        ContractRequestDTO updateDTO = new ContractRequestDTO(
                1, "Updated Contract", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31), new BigDecimal("1200.00"), "USD"
        );
        Contract updatedContract = Contract.builder()
                .id(1)
                .customer(customer)
                .title("Updated Contract")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .monthlyFee(new BigDecimal("1200.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(contractRepository.findById(1)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(updatedContract);

        // When
        ContractResponseDTO result = contractService.update(1, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Updated Contract", result.title());
        verify(contractRepository, times(1)).findById(1);
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenContractDoesNotExist() {
        // Given
        when(contractRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.update(1, requestDTO));
        assertEquals("Contract not found with ID: 1", exception.getMessage());
        verify(contractRepository, times(1)).findById(1);
        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    void delete_ShouldDeleteContract_WhenContractExists() {
        // Given
        when(contractRepository.existsById(1)).thenReturn(true);

        // When
        contractService.delete(1);

        // Then
        verify(contractRepository, times(1)).existsById(1);
        verify(contractRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenContractDoesNotExist() {
        // Given
        when(contractRepository.existsById(1)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.delete(1));
        assertEquals("Contract not found with ID: 1", exception.getMessage());
        verify(contractRepository, times(1)).existsById(1);
        verify(contractRepository, never()).deleteById(anyInt());
    }

    @Test
    void findByCustomerId_ShouldReturnListOfContracts_WhenCustomerHasContracts() {
        // Given
        when(contractRepository.findByCustomerId(1)).thenReturn(List.of(contract));

        // When
        List<ContractResponseDTO> result = contractService.findByCustomerId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO.customerId(), result.get(0).customerId());
        verify(contractRepository, times(1)).findByCustomerId(1);
    }

    @Test
    void findByCustomerId_ShouldReturnEmptyList_WhenCustomerHasNoContracts() {
        // Given
        when(contractRepository.findByCustomerId(1)).thenReturn(List.of());

        // When
        List<ContractResponseDTO> result = contractService.findByCustomerId(1);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(contractRepository, times(1)).findByCustomerId(1);
    }

    @Test
    void findByStatus_ShouldReturnListOfContracts_WhenStatusExists() {
        // Given
        when(contractRepository.findByStatus("ACTIVE")).thenReturn(List.of(contract));

        // When
        List<ContractResponseDTO> result = contractService.findByStatus("ACTIVE");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).status());
        verify(contractRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    void findByStatus_ShouldReturnEmptyList_WhenNoContractsWithStatusExist() {
        // Given
        when(contractRepository.findByStatus("INACTIVE")).thenReturn(List.of());

        // When
        List<ContractResponseDTO> result = contractService.findByStatus("INACTIVE");

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(contractRepository, times(1)).findByStatus("INACTIVE");
    }

}
