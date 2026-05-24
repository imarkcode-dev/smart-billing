package com.smart.billing.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractRequestDTO(

   @NotNull(message = "Customer ID is required")
    Integer customerId,
    
    String title,
    
    @NotNull(message = "Start date is required")
    LocalDate startDate,
    
    LocalDate endDate,
    
    @NotNull(message = "Monthly fee is required")
    @Positive(message = "Monthly fee must be positive")
    BigDecimal monthlyFee,
    
    String currency 

) {}
