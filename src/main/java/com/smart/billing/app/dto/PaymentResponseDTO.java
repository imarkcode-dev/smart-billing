package com.smart.billing.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
    Integer id,
    Integer invoiceId,
    String invoiceNumber,
    LocalDateTime paymentDate,
    BigDecimal amountPaid,
    String paymentMethod,
    String referenceNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

