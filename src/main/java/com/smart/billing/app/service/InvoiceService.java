package com.smart.billing.app.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.billing.app.domain.Contract;
import com.smart.billing.app.domain.Invoice;
import com.smart.billing.app.dto.InvoiceRequestDTO;
import com.smart.billing.app.dto.InvoiceResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.ContractRepository;
import com.smart.billing.app.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for managing billing invoices.
 * Handles business logic for creating, updating, and retrieving invoice records,
 * ensuring synchronization with contract financial data.
 */
@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {

     private static final Logger LOG = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;

    /**
     * Retrieves all invoices from the database.
     *
     * @return A list of {@link InvoiceResponseDTO} representing all invoices.
     */
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> findAll() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Finds a specific invoice by its unique identifier.
     *
     * @param id The ID of the invoice to retrieve.
     * @return The found {@link InvoiceResponseDTO}.
     * @throws ResourceNotFoundException if no invoice is found with the given ID.
     */
    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO findById(Integer id) {
        return invoiceRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    /**
     * Creates a new invoice based on contract information.
     * Validates contract existence and ensures the invoice number is unique.
     * The total amount is automatically synchronized with the contract's monthly fee.
     *
     * @param dto The data transfer object containing invoice details.
     * @return The created {@link InvoiceResponseDTO}.
     * @throws ResourceNotFoundException if the associated contract does not exist.
     * @throws ResourceNotFoundException  if the invoice number is already registered.
     */
    @Override
    @Transactional
    public InvoiceResponseDTO create(InvoiceRequestDTO dto) {
        LOG.debug("InvoiceService:: create");
        Contract contract = contractRepository.findById(dto.contractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + dto.contractId()));

        if (invoiceRepository.existsByInvoiceNumber(dto.invoiceNumber())) {
            throw new ResourceNotFoundException ("Invoice number " + dto.invoiceNumber() + " already exists.");
        }

        Invoice invoice = Invoice.builder()
                .contract(contract)
                .invoiceNumber(dto.invoiceNumber())
                .issueDate(dto.issueDate())
                .dueDate(dto.dueDate())
                .totalAmount(dto.totalAmount() != null ? dto.totalAmount() : BigDecimal.ZERO) 
                .penaltyAmount(dto.penaltyAmount() != null ? dto.penaltyAmount() : BigDecimal.ZERO)
                .status( dto.status() != null ? dto.status() : "PENDING")
                .build();

        return mapToResponse(invoiceRepository.save(invoice));
    }

    /**
     * Updates an existing invoice. 
     * If the contract is changed, the total amount is recalculated based on the new contract fee.
     * Validates that the new invoice number is not in use by other records.
     *
     * @param id The ID of the invoice to update.
     * @param dto The updated data.
     * @return The updated {@link InvoiceResponseDTO}.
     * @throws ResourceNotFoundException if the invoice or new contract is not found.
     */
    @Override
    @Transactional
    public InvoiceResponseDTO update(Integer id, InvoiceRequestDTO dto) {
        LOG.debug("InvoiceService:: udpdate");
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        // Note: Using contract search to ensure business rules across relationships
        Contract currentContract = contractRepository.findById(dto.contractId())
            .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        // Update contract and fee if a different contract is provided
        if (!invoice.getContract().getId().equals(dto.contractId())) {
            invoice.setContract(currentContract);
        }

        // Validate invoice number uniqueness upon change
        if (!invoice.getInvoiceNumber().equals(dto.invoiceNumber())) {
            if (invoiceRepository.existsByInvoiceNumber(dto.invoiceNumber())) {
                throw new ResourceNotFoundException("Invoice number " + dto.invoiceNumber() + " is already in use.");
            }
            invoice.setInvoiceNumber(dto.invoiceNumber());
        }

        invoice.setIssueDate(dto.issueDate());
        invoice.setDueDate(dto.dueDate());
        invoice.setTotalAmount(dto.totalAmount() != null ? dto.totalAmount() : BigDecimal.ZERO); 
        invoice.setPenaltyAmount(dto.penaltyAmount() != null ? dto.penaltyAmount() : BigDecimal.ZERO);
        invoice.setStatus(dto.status() != null ? dto.status() : "PENDING");

        Invoice invoiceUpdated = invoiceRepository.save(invoice);
        return mapToResponse(invoiceUpdated);
    }

    /**
     * Deletes an invoice record by ID.
     *
     * @param id The ID of the invoice to remove.
     * @throws ResourceNotFoundException if the invoice does not exist.
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice not found with ID: " + id);
        }
        invoiceRepository.deleteById(id);
    }

    /**
     * Internal mapper to convert an Invoice entity to a Response DTO.
     *
     * @param entity The source {@link Invoice} entity.
     * @return A {@link InvoiceResponseDTO} containing flattened data for the API.
     */
    private InvoiceResponseDTO mapToResponse(Invoice entity) {
        return new InvoiceResponseDTO(
                entity.getId(),
                entity.getContract().getId(),
                entity.getInvoiceNumber(),
                entity.getContract().getCustomer().getNameCustomer(),
                entity.getIssueDate(),
                entity.getDueDate(),
                entity.getTotalAmount(),
                entity.getPenaltyAmount(),
                entity.getStatus()
        );
    }
}
