package com.smart.billing.app.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.billing.app.domain.Contract;
import com.smart.billing.app.domain.Customer;
import com.smart.billing.app.dto.ContractRequestDTO;
import com.smart.billing.app.dto.ContractResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.ContractRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service layer implementation for managing contracts.
 * Provides CRUD operations and query methods for contracts,
 * converting between Contract entities and ContractResponseDTOs.
 *
 * This class interacts with the ContractRepository to perform
 * persistence operations and applies business rules such as
 * default values and validation.
 */
@Service
@RequiredArgsConstructor
public class ContractService implements IContractService {

    private final ContractRepository contractRepository;

    /**
     * Retrieve all contracts from the database.
     *
     * @return List of ContractResponseDTO representing all contracts
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContractResponseDTO> findAll() {
        return contractRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    /**
     * Retrieve a contract by its ID.
     *
     * @param id Contract ID
     * @return ContractResponseDTO if found
     * @throws ResourceNotFoundException if contract does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public ContractResponseDTO findById(Integer id) {
        return contractRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
    }

    /**
     * Create a new contract based on the provided DTO.
     * Assigns default values for status ("ACTIVE") and currency ("USD") if not provided.
     *
     * @param dto ContractRequestDTO containing contract details
     * @return Created contract as ContractResponseDTO
     * @throws ResourceNotFoundException if DTO is null
     */
    @Override
    @Transactional
    public ContractResponseDTO create(ContractRequestDTO dto) {
        if (dto == null) {
            throw new ResourceNotFoundException("ContractRequestDTO cannot be null");
        }

        Customer customer = Customer.builder()
            .id(dto.customerId())
            .build();

        Contract contractEntity = Contract.builder()
            .customer(customer)
            .title(dto.title())
            .startDate(dto.startDate())
            .endDate(dto.endDate())
            .monthlyFee(dto.monthlyFee())
            .status("ACTIVE")
            .currency(dto.currency() != null ? dto.currency() : "USD")
            .build();

        Contract contractSaved = contractRepository.save(contractEntity);
        return mapToResponse(contractSaved);
    }

    /**
     * Update an existing contract by ID with new details from the DTO.
     *
     * @param id Contract ID
     * @param dto ContractRequestDTO containing updated details
     * @return Updated contract as ContractResponseDTO
     * @throws ResourceNotFoundException if contract does not exist
     */
    @Override
    @Transactional
    public ContractResponseDTO update(Integer id, ContractRequestDTO dto) {
        Contract contractEntity = contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + id));

        Customer customer = Customer.builder()
            .id(dto.customerId())
            .build();

        contractEntity.setCustomer(customer);
        contractEntity.setTitle(dto.title());
        contractEntity.setStartDate(dto.startDate());
        contractEntity.setEndDate(dto.endDate());
        contractEntity.setMonthlyFee(dto.monthlyFee());
        contractEntity.setCurrency(dto.currency());

        Contract contractUpdated = contractRepository.save(contractEntity);
        return mapToResponse(contractUpdated);
    }

    /**
     * Delete a contract by its ID.
     *
     * @param id Contract ID
     * @throws ResourceNotFoundException if contract does not exist
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!contractRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contract not found with ID: " + id);
        }
        contractRepository.deleteById(id);
    }

    /**
     * Retrieve contracts by customer ID.
     *
     * @param id Customer ID
     * @return List of ContractResponseDTO belonging to the specified customer
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContractResponseDTO> findByCustomerId(Integer id) {
        return contractRepository.findByCustomerId(id)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    /**
     * Retrieve contracts by status.
     *
     * @param status Contract status (e.g., ACTIVE, INACTIVE)
     * @return List of ContractResponseDTO with the specified status
     */
    @Transactional(readOnly = true)
    public List<ContractResponseDTO> findByStatus(String status) {
        return contractRepository.findByStatus(status)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    /**
     * Helper method to convert a Contract entity into a ContractResponseDTO.
     *
     * @param entity Contract entity
     * @return ContractResponseDTO with mapped fields
     */
    private ContractResponseDTO mapToResponse(Contract entity) {
        return new ContractResponseDTO(
            entity.getId(),
            entity.getCustomer().getId(),
            entity.getCustomer().getNameCustomer(),
            entity.getTitle(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getMonthlyFee(),
            entity.getCurrency(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
