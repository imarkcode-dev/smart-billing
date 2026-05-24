package com.smart.billing.app.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.billing.app.domain.Customer;
import com.smart.billing.app.dto.CustomerRequestDTO;
import com.smart.billing.app.dto.CustomerResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for managing customer operations in the Smart Billing application.
 *
 * This service provides business logic for CRUD operations on customers, including
 * validation, data transformation, and transaction management. It implements the
 * ICustomerService interface and handles the conversion between domain entities
 * and DTOs for the REST API layer.
 *
 * Key features:
 * - Tax ID uniqueness validation during creation and updates
 * - Automatic risk score initialization for new customers
 * - Default "ACTIVE" status for new customers
 * - Transactional operations for data consistency
 * - Comprehensive error handling with meaningful messages
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Retrieves all customers from the database.
     *
     * @return List of CustomerResponseDTO containing all customers in the system
     */
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    /**
     * Retrieves a specific customer by their unique identifier.
     *
     * @param id the unique identifier of the customer to retrieve
     * @return CustomerResponseDTO containing the customer information
     * @throws ResourceNotFoundException if no customer is found with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO findById(Integer id) {
        return customerRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow( () -> new ResourceNotFoundException("Customer not found with id: " + id) );
    }

    /**
     * Creates a new customer in the system.
     *
     * This method performs validation to ensure the tax ID is unique and sets
     * default values for risk score (0.00) and status ("ACTIVE").
     *
     * @param dto the CustomerRequestDTO containing the customer information to create
     * @return CustomerResponseDTO containing the created customer information
     * @throws ResourceNotFoundException if the request DTO is null or tax ID already exists
     */
    @Override
    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO dto) {

       if(dto == null) {
         throw new ResourceNotFoundException("CustomerRequestDTO cannot be null");
       }

       if (customerRepository.existsByTaxId(dto.taxId())) {
        throw new ResourceNotFoundException("The Tax ID " + dto.taxId() + " is already registered.");
       }

        Customer customerEntity = Customer.builder()
                .taxId(dto.taxId())
                .nameCustomer(dto.nameCustomer())
                .email(dto.email())
                .phone(dto.phone())
                .riskScore(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();

       Customer customerSaved = customerRepository.save(customerEntity);

       return mapToResponse(customerSaved);


    }

    /**
     * Updates an existing customer with new information.
     *
     * This method validates that the customer exists and ensures tax ID uniqueness
     * if the tax ID is being changed. Only the tax ID, name, email, and phone can be updated.
     *
     * @param id the unique identifier of the customer to update
     * @param dto the CustomerRequestDTO containing the updated customer information
     * @return CustomerResponseDTO containing the updated customer information
     * @throws ResourceNotFoundException if the customer is not found or tax ID is already used by another customer
     */
    @Override
    @Transactional
    public CustomerResponseDTO update(Integer id, CustomerRequestDTO dto) {

        Customer customerEntity = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        if (!customerEntity.getTaxId().equals(dto.taxId())) {
            if (customerRepository.existsByTaxId(dto.taxId())) {
                throw new ResourceNotFoundException("The Tax ID " + dto.taxId() + " is already used by another customer.");
            }
            customerEntity.setTaxId(dto.taxId());
        }

        customerEntity.setNameCustomer(dto.nameCustomer());
        customerEntity.setEmail(dto.email());
        customerEntity.setPhone(dto.phone());

        Customer customerUpdated = customerRepository.save(customerEntity);

        return mapToResponse(customerUpdated);
    }


    /**
     * Deletes a customer from the system by their unique identifier.
     *
     * @param id the unique identifier of the customer to delete
     * @throws ResourceNotFoundException if no customer is found with the given ID
     */
    @Override
    @Transactional
    public void delete(Integer id) {

        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    /**
     * Maps a Customer entity to a CustomerResponseDTO.
     *
     * This private method is used internally to convert domain entities
     * to response DTOs for the REST API layer.
     *
     * @param entity the Customer entity to map
     * @return CustomerResponseDTO containing the mapped customer data
     */
    private CustomerResponseDTO mapToResponse(Customer entity) {
        return new CustomerResponseDTO(
            entity.getId(),
            entity.getTaxId(),
            entity.getNameCustomer(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getRiskScore(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Maps a CustomerResponseDTO to a Customer entity.
     *
     * This private method is used internally to convert response DTOs
     * back to domain entities when needed.
     *
     * @param dto the CustomerResponseDTO to map
     * @return Customer entity containing the mapped customer data
     */
    private Customer mapToEntity(CustomerResponseDTO dto) {
            Customer entity  = new Customer();

            entity.setId(dto.id());
            entity.setTaxId(dto.taxId());
            entity.setNameCustomer(dto.nameCustomer());
            entity.setEmail(dto.email());
            entity.setPhone(dto.phone());
            entity.setRiskScore(dto.riskScore());
            entity.setStatus(dto.status());
            entity.setCreatedAt(dto.createdAt());
            entity.setUpdatedAt(dto.updatedAt());
            return entity;
    }



}
