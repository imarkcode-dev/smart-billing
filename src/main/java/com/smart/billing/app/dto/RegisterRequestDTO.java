package com.smart.billing.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotBlank String userName,
    @NotBlank String lastName,
    String authProvider, 
    String roleUser
) {}
