package com.smart.billing.app.dto;

public record LoginResponseDTO(
    Integer id,
    String email,
    String username,
    String lastName,
    String authProvider,
    String roleUser,
    String lastLogin,
    String token
) {}
