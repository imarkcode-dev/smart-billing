package com.smart.billing.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequestDTO(

    @NotNull(message = "Invoice ID is required")
    Integer invoiceId,

    LocalDateTime paymentDate,

    @NotNull(message = "Amount paid is required")
    
    @Positive(message = "Amount must be greater than zero")
    
    BigDecimal amountPaid,
    
    String paymentMethod,

    String referenceNumber
    
) { }
