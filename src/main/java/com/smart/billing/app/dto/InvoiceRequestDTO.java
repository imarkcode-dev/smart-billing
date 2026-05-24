package com.smart.billing.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvoiceRequestDTO(

    @NotNull(message = "Contract ID is required")
    Integer contractId,

    @NotBlank(message = "Invoice number is required")
    String invoiceNumber,

    @NotNull(message = "Issue date is required")
    LocalDateTime  issueDate,

    @NotNull(message = "Due date is required")
    LocalDateTime  dueDate,

    BigDecimal totalAmount,

    BigDecimal penaltyAmount,

    String status

    
) { }
