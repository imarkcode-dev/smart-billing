package com.smart.billing.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.billing.app.domain.Invoice;
import com.smart.billing.app.domain.Payment;
import com.smart.billing.app.dto.PaymentRequestDTO;
import com.smart.billing.app.dto.PaymentResponseDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.InvoiceRepository;
import com.smart.billing.app.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;

    private final InvoiceRepository invoiceRepository;


    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findAllInvoice() {
        return paymentRepository.findAllInvoiceWithInvoice().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO findById(Integer id) {
        return paymentRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow( () -> new ResourceNotFoundException("Payment not found with id: " + id) );
    }

    

    /**
     * Registers a new payment and updates the associated invoice status to PAID.
     * 
     * @param dto Payment details.
     * @return Saved payment information.
     */
    @Transactional
    public PaymentResponseDTO create(PaymentRequestDTO dto) {

        Invoice invoice = invoiceRepository.findById(dto.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // Business Rule: Update invoice status upon payment
        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amountPaid(dto.amountPaid())
                .paymentMethod(dto.paymentMethod())
                .referenceNumber(dto.referenceNumber())
                .build();

        return mapToResponse(paymentRepository.save(payment));
    }


    @Override
    @Transactional
    public PaymentResponseDTO update(Integer id, PaymentRequestDTO dto) {

        if (dto == null) {
            throw new ResourceNotFoundException("PaymentRequestDTO cannot be null");
        }

        // Validate that the payment exists
        Payment paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id ));

        // Validate that the invoice exists (if changed)
        if (!paymentEntity.getInvoice().getId().equals(dto.invoiceId())) {
            Invoice invoice = invoiceRepository.findById(dto.invoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + dto.invoiceId()));
            paymentEntity.setInvoice(invoice);
        }

        // Update fields
        paymentEntity.setPaymentDate(dto.paymentDate());
        paymentEntity.setAmountPaid(dto.amountPaid());
        paymentEntity.setPaymentMethod(dto.paymentMethod());
        paymentEntity.setReferenceNumber(dto.referenceNumber());

        // Persist changes
        Payment paymentUpdated = paymentRepository.save(paymentEntity);

        // Map to response DTO
        return new PaymentResponseDTO(
                paymentUpdated.getId(),
                paymentUpdated.getInvoice().getId(),
                paymentUpdated.getInvoice().getInvoiceNumber(),
                paymentUpdated.getPaymentDate(),
                paymentUpdated.getAmountPaid(),
                paymentUpdated.getPaymentMethod(),
                paymentUpdated.getReferenceNumber(),
                paymentUpdated.getCreatedAt(),
                paymentUpdated.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
    }


    private PaymentResponseDTO mapToResponse(Payment entity) {
    return new PaymentResponseDTO(
        entity.getId(),
        entity.getInvoice().getId(),
        entity.getInvoice().getInvoiceNumber(),
        entity.getPaymentDate(),          
        entity.getAmountPaid(),          
        entity.getPaymentMethod(),
        entity.getReferenceNumber(),
        entity.getCreatedAt(),           
        entity.getUpdatedAt() 
    );
}

    public List<PaymentResponseDTO> findAllWithInvoice() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllWithInvoice'");
    }


}
