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

import com.smart.billing.app.dto.ContractRequestDTO;
import com.smart.billing.app.dto.ContractResponseDTO;
import com.smart.billing.app.service.IContractService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


/**
 * REST controller that exposes endpoints for managing contracts.
 * Provides CRUD operations and query endpoints for contracts by customer and status.
 * 
 * Base URL: /api/v1/contract
 */
@RestController
@CrossOrigin(origins = "*") // Allows cross-origin requests from any domain
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {

    private final IContractService contractService;

    /**
     * Retrieve all contracts.
     * 
     * @return List of all contracts as ContractResponseDTO
     */
    @GetMapping
    public ResponseEntity<List<ContractResponseDTO>> getAll() {
        return ResponseEntity.ok(contractService.findAll());
    }

    /**
     * Retrieve a contract by its ID.
     * 
     * @param id Contract ID
     * @return ContractResponseDTO if found, otherwise throws ResourceNotFoundException
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContractResponseDTO> getContractById(@PathVariable Integer id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    /**
     * Create a new contract.
     * 
     * @param dto ContractRequestDTO containing contract details
     * @return Created contract as ContractResponseDTO with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<ContractResponseDTO> createContract(@Valid @RequestBody ContractRequestDTO dto) {
        ContractResponseDTO created = contractService.create(dto);      
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing contract by ID.
     * 
     * @param id Contract ID
     * @param dto ContractRequestDTO containing updated contract details
     * @return Updated contract as ContractResponseDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContractResponseDTO> updateContract(
        @PathVariable Integer id, @Valid @RequestBody ContractRequestDTO dto) {
        return ResponseEntity.ok(contractService.update(id, dto));
    }

    /**
     * Delete a contract by ID.
     * 
     * @param id Contract ID
     * @return HTTP 204 No Content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Integer id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve contracts by customer ID.
     * 
     * @param customerId Customer ID
     * @return List of contracts belonging to the specified customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ContractResponseDTO>> getContractsByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(contractService.findByCustomerId(customerId));
    }

    /**
     * Retrieve contracts by status.
     * 
     * @param status Contract status (e.g., ACTIVE, INACTIVE)
     * @return List of contracts with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContractResponseDTO>> getContractsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(contractService.findByStatus(status));
    }
}
