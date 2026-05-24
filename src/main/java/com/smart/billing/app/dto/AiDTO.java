package com.smart.billing.app.dto;

import java.util.List;

import java.math.BigDecimal;

public class AiDTO {

    public record RiskPredictionResponse(
        String taxId,
        BigDecimal riskScore,
        String riskLevel, 
        String justification
    ) {}

    public record CollectionStrategyResponse(
        String recommendedAction, 
        String priority,       
        String tacticalReasoning
    ) {}

    public record CustomizedReminderResponse(
        String emailSubject,
        String emailBody,
        String smsBody
    ) {}

    public record ExecutiveSummaryResponse(
        String summaryText,
        List<String> highRiskCustomers,
        String criticalAlerts
    ) {}

}
