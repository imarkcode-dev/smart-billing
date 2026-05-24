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

import com.smart.billing.app.dto.InvoiceRequestDTO;
import com.smart.billing.app.dto.InvoiceResponseDTO;
import com.smart.billing.app.service.IInvoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing Invoice resources.
 * Provides endpoints for CRUD operations related to billing invoices.
 * Access is mapped to the base path "/api/v1/invoice".
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceService invoiceService;

    /**
     * Retrieves a list of all invoices.
     *
     * @return {@link ResponseEntity} containing a list of {@link InvoiceResponseDTO} 
     *         and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAll() {
        return ResponseEntity.ok(invoiceService.findAll());
    }

    /**
     * Retrieves a specific invoice by its unique identifier.
     *
     * @param id The unique ID of the invoice.
     * @return {@link ResponseEntity} containing the found {@link InvoiceResponseDTO} 
     *         and HTTP status 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    /**
     * Creates a new invoice record.
     * Validates the input request body before processing.
     *
     * @param request The {@link InvoiceRequestDTO} containing the data for the new invoice.
     * @return {@link ResponseEntity} containing the created {@link InvoiceResponseDTO} 
     *         and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> create(@Valid @RequestBody InvoiceRequestDTO request) {
        InvoiceResponseDTO response = invoiceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing invoice identified by its ID.
     *
     * @param id      The ID of the invoice to be updated.
     * @param request The {@link InvoiceRequestDTO} containing the updated information.
     * @return {@link ResponseEntity} containing the updated {@link InvoiceResponseDTO} 
     *         and HTTP status 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> update(
            @PathVariable Integer id, 
            @Valid @RequestBody InvoiceRequestDTO request) {
        
        InvoiceResponseDTO response = invoiceService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an invoice from the system.
     *
     * @param id The unique ID of the invoice to remove.
     * @return {@link ResponseEntity} with HTTP status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

}