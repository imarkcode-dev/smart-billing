package com.smart.billing.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.billing.app.dto.CustomerRequestDTO;
import com.smart.billing.app.dto.CustomerResponseDTO;
import com.smart.billing.app.service.ICustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing customer operations in the Smart Billing application.
 *
 * This controller provides CRUD (Create, Read, Update, Delete) operations for customers,
 * including endpoints to retrieve all customers, get a specific customer by ID,
 * create new customers, update existing customers, and delete customers.
 *
 * All endpoints are accessible via the base path "/api/v1/customer" and support
 * cross-origin requests from any origin.
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    /**
     * Retrieves all customers from the system.
     *
     * @return ResponseEntity containing a list of all CustomerResponseDTO objects
     *         with HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    /**
     * Retrieves a specific customer by their unique identifier.
     *
     * @param id the unique identifier of the customer to retrieve
     * @return ResponseEntity containing the CustomerResponseDTO for the specified customer
     *         with HTTP 200 OK status
     * @throws ResourceNotFoundException if no customer is found with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    /**
     * Creates a new customer in the system.
     *
     * @param request the CustomerRequestDTO containing the customer information to create
     * @return ResponseEntity containing the created CustomerResponseDTO
     *         with HTTP 201 Created status
     * @throws ConstraintViolationException if the request data violates validation constraints
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing customer with new information.
     *
     * @param id the unique identifier of the customer to update
     * @param request the CustomerRequestDTO containing the updated customer information
     * @return ResponseEntity containing the updated CustomerResponseDTO
     *         with HTTP 200 OK status
     * @throws ResourceNotFoundException if no customer is found with the given ID
     * @throws ConstraintViolationException if the request data violates validation constraints
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerRequestDTO request) {

        CustomerResponseDTO response = customerService.update(id, request);

         return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    /**
     * Deletes a customer from the system.
     *
     * @param id the unique identifier of the customer to delete
     * @return ResponseEntity with HTTP 204 No Content status indicating successful deletion
     * @throws ResourceNotFoundException if no customer is found with the given ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}   
