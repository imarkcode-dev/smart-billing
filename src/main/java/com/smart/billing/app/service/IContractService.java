package com.smart.billing.app.service;

import java.util.List;

import com.smart.billing.app.dto.ContractRequestDTO;
import com.smart.billing.app.dto.ContractResponseDTO;

/**
 * Service interface for contract management operations in the Smart Billing application.
 *
 * This interface defines the business logic contract for handling customer agreements,
 * including lifecycle management (creation, updates, deletion) and various search
 * capabilities. It ensures loose coupling by using DTOs for data transfer.
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
public interface IContractService {

    /**
     * Retrieves all contracts available in the system.
     *
     * @return List of ContractResponseDTO containing all contract records.
     */
    List<ContractResponseDTO> findAll();

    /**
     * Retrieves a specific contract by its unique identifier.
     *
     * @param id the unique identifier of the contract
     * @return ContractResponseDTO containing the contract details
     * @throws ResourceNotFoundException if no contract is found with the given ID
     */
    ContractResponseDTO findById(Integer id);

    /**
     * Creates and persists a new contract in the system.
     *
     * @param dto the ContractRequestDTO containing the details of the contract to create
     * @return ContractResponseDTO containing the created contract information
     */
    ContractResponseDTO create(ContractRequestDTO dto);

    /**
     * Updates an existing contract's information.
     *
     * @param id the unique identifier of the contract to update
     * @param dto the ContractRequestDTO containing the updated information
     * @return ContractResponseDTO containing the updated contract data
     * @throws ResourceNotFoundException if the contract is not found
     */
    ContractResponseDTO update(Integer id, ContractRequestDTO dto);

    /**
     * Deletes a contract from the system.
     *
     * @param id the unique identifier of the contract to delete
     * @throws ResourceNotFoundException if the contract does not exist
     */
    void delete(Integer id);

    /**
     * Retrieves all contracts belonging to a specific customer.
     *
     * This method is useful for filtering contracts by their owner/client.
     *
     * @param customerId the ID of the customer whose contracts should be retrieved
     * @return List of ContractResponseDTO associated with the specified customer
     */
    List<ContractResponseDTO> findByCustomerId(Integer customerId);

    /**
     * Retrieves contracts filtered by their current status.
     *
     * Common status values include "ACTIVE", "INACTIVE", or "EXPIRED".
     *
     * @param status the status string to filter by
     * @return List of ContractResponseDTO matching the given status
     */
    List<ContractResponseDTO> findByStatus(String status);

}
