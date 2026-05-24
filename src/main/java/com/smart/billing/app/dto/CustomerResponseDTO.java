package com.smart.billing.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerResponseDTO(
    Integer id,
    String taxId,
    String nameCustomer,
    String email,
    String phone,
    BigDecimal riskScore,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {}
