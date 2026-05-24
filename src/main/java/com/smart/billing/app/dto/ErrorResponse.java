package com.smart.billing.app.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timestamp,
    int Status,
    String message
) {}
