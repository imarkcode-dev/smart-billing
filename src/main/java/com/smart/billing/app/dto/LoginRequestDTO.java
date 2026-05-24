package com.smart.billing.app.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "email is required.")
    String email, 

    @NotBlank(message = "password is required.")
    String password
) {}
