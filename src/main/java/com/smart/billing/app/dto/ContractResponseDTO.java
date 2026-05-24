package com.smart.billing.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContractResponseDTO(

    Integer id,
    Integer customerId,
    String customerName,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal monthlyFee,
    String currency,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt


) {}
