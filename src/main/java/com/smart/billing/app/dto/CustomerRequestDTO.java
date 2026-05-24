package com.smart.billing.app.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(

    @NotBlank(message = "Tax Id is required.")
    String taxId,

    @NotBlank(message = "Name Customer is required.")
    String nameCustomer,

    String email,
    
    String phone
) {}
