package com.smart.billing.app.controller;

import java.util.List;

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

import com.smart.billing.app.dto.PaymentRequestDTO;
import com.smart.billing.app.dto.PaymentResponseDTO;
import com.smart.billing.app.service.IPaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    /**
     * Get all payments.
     *
     * @return List of PaymentResponseDTO
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.findAllInvoice());
    }

    /**
     * Get a payment by ID.
     *
     * @param id Payment ID
     * @return PaymentResponseDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    /**
     * Create a new payment.
     *
     * @param dto PaymentRequestDTO
     * @return PaymentResponseDTO
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(paymentService.create(dto));
    }

    /**
     * Update an existing payment.
     *
     * @param id Payment ID
     * @param dto PaymentRequestDTO
     * @return PaymentResponseDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable Integer id,
                                                            @Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    /**
     * Delete a payment by ID.
     *
     * @param id Payment ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
