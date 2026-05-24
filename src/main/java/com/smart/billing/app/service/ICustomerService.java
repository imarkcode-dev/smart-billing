package com.smart.billing.app.service;

import java.util.List;

import com.smart.billing.app.dto.CustomerResponseDTO;
import com.smart.billing.app.dto.CustomerRequestDTO;

/**
 * Service interface for customer management operations in the Smart Billing application.
 *
 * This interface defines the contract for customer-related business operations,
 * providing a clear separation between the service layer and its implementations.
 * It supports full CRUD (Create, Read, Update, Delete) operations for customer entities.
 *
 * All methods work with DTOs (Data Transfer Objects) to maintain loose coupling
 * between the service layer and the presentation layer, ensuring clean architecture
 * and testability.
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
public interface ICustomerService {

    /**
     * Retrieves all customers from the system.
     *
     * @return List of CustomerResponseDTO containing all customers in the system.
     *         Returns an empty list if no customers exist.
     */
    List<CustomerResponseDTO> findAll();

    /**
     * Retrieves a specific customer by their unique identifier.
     *
     * @param id the unique identifier of the customer to retrieve
     * @return CustomerResponseDTO containing the customer information
     * @throws ResourceNotFoundException if no customer is found with the given ID
     */
    CustomerResponseDTO findById(Integer id);

    /**
     * Creates a new customer in the system.
     *
     * @param dto the CustomerRequestDTO containing the customer information to create
     * @return CustomerResponseDTO containing the created customer information with generated ID
     * @throws ResourceNotFoundException if the request is invalid or violates business rules
     */
    CustomerResponseDTO create(CustomerRequestDTO dto);

    /**
     * Updates an existing customer with new information.
     *
     * @param id the unique identifier of the customer to update
     * @param dto the CustomerRequestDTO containing the updated customer information
     * @return CustomerResponseDTO containing the updated customer information
     * @throws ResourceNotFoundException if the customer is not found or update violates business rules
     */
    CustomerResponseDTO update(Integer id, CustomerRequestDTO dto);

    /**
     * Deletes a customer from the system.
     *
     * @param id the unique identifier of the customer to delete
     * @throws ResourceNotFoundException if no customer is found with the given ID
     */
    void delete(Integer id);

}
