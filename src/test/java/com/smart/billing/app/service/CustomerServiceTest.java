/*
Generate unit tests for CustomerService using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock EmployeeRepository
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

import com.smart.billing.app.domain.Customer;
import com.smart.billing.app.dto.CustomerRequestDTO;
import com.smart.billing.app.dto.CustomerResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceTest Unit Tests")
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequestDTO requestDTO;
    private CustomerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1)
                .taxId("123456789")
                .nameCustomer("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .riskScore(BigDecimal.ZERO)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = new CustomerRequestDTO("123456789", "John Doe", "john.doe@example.com", "123-456-7890");

        responseDTO = new CustomerResponseDTO(
                1, "123456789", "John Doe", "john.doe@example.com", "123-456-7890",
                BigDecimal.ZERO, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void findAll_ShouldReturnListOfCustomerResponseDTO() {
        // Given
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        // When
        List<CustomerResponseDTO> result = customerService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO.id(), result.get(0).id());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnCustomerResponseDTO_WhenCustomerExists() {
        // Given
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        // When
        CustomerResponseDTO result = customerService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO.id(), result.id());
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        // Given
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.findById(1));
        assertEquals("Customer not found with id: 1", exception.getMessage());
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    void create_ShouldReturnCustomerResponseDTO_WhenValidRequest() {
        // Given
        when(customerRepository.existsByTaxId("123456789")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        CustomerResponseDTO result = customerService.create(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO.taxId(), result.taxId());
        verify(customerRepository, times(1)).existsByTaxId("123456789");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenRequestDTOIsNull() {
        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.create(null));
        assertEquals("CustomerRequestDTO cannot be null", exception.getMessage());
        verify(customerRepository, never()).existsByTaxId(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenTaxIdAlreadyExists() {
        // Given
        when(customerRepository.existsByTaxId("123456789")).thenReturn(true);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.create(requestDTO));
        assertEquals("The Tax ID 123456789 is already registered.", exception.getMessage());
        verify(customerRepository, times(1)).existsByTaxId("123456789");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void update_ShouldReturnCustomerResponseDTO_WhenCustomerExistsAndTaxIdUnchanged() {
        // Given
        CustomerRequestDTO updateDTO = new CustomerRequestDTO("123456789", "Jane Doe", "jane.doe@example.com", "098-765-4321");
        Customer updatedCustomer = Customer.builder()
                .id(1)
                .taxId("123456789")
                .nameCustomer("Jane Doe")
                .email("jane.doe@example.com")
                .phone("098-765-4321")
                .riskScore(BigDecimal.ZERO)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // When
        CustomerResponseDTO result = customerService.update(1, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Jane Doe", result.nameCustomer());
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, never()).existsByTaxId(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void update_ShouldReturnCustomerResponseDTO_WhenCustomerExistsAndTaxIdChanged() {
        // Given
        CustomerRequestDTO updateDTO = new CustomerRequestDTO("987654321", "Jane Doe", "jane.doe@example.com", "098-765-4321");
        Customer updatedCustomer = Customer.builder()
                .id(1)
                .taxId("987654321")
                .nameCustomer("Jane Doe")
                .email("jane.doe@example.com")
                .phone("098-765-4321")
                .riskScore(BigDecimal.ZERO)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByTaxId("987654321")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // When
        CustomerResponseDTO result = customerService.update(1, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("987654321", result.taxId());
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, times(1)).existsByTaxId("987654321");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        // Given
        CustomerRequestDTO updateDTO = new CustomerRequestDTO("123456789", "Jane Doe", "jane.doe@example.com", "098-765-4321");
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.update(1, updateDTO));
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, never()).existsByTaxId(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTaxIdAlreadyUsedByAnotherCustomer() {
        // Given
        CustomerRequestDTO updateDTO = new CustomerRequestDTO("987654321", "Jane Doe", "jane.doe@example.com", "098-765-4321");
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByTaxId("987654321")).thenReturn(true);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.update(1, updateDTO));
        assertEquals("The Tax ID 987654321 is already used by another customer.", exception.getMessage());
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, times(1)).existsByTaxId("987654321");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void delete_ShouldDeleteCustomer_WhenCustomerExists() {
        // Given
        when(customerRepository.existsById(1)).thenReturn(true);

        // When
        customerService.delete(1);

        // Then
        verify(customerRepository, times(1)).existsById(1);
        verify(customerRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        // Given
        when(customerRepository.existsById(1)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.delete(1));
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository, times(1)).existsById(1);
        verify(customerRepository, never()).deleteById(anyInt());
    }
}
