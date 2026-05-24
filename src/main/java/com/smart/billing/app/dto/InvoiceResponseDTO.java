package com.smart.billing.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponseDTO(
    Integer id,
    Integer contractId,
    String invoiceNumber,
    String customerName,
    LocalDateTime issueDate,
    LocalDateTime dueDate,
    BigDecimal totalAmount,
    BigDecimal penaltyAmount,
    String status
    
) { }
