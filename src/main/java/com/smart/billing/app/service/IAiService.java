package com.smart.billing.app.service;

import com.smart.billing.app.dto.AiDTO.CollectionStrategyResponse;
import com.smart.billing.app.dto.AiDTO.CustomizedReminderResponse;
import com.smart.billing.app.dto.AiDTO.ExecutiveSummaryResponse;
import com.smart.billing.app.dto.AiDTO.RiskPredictionResponse;

public interface IAiService {

    RiskPredictionResponse predictCustomerRisk(Integer customerId);

    CollectionStrategyResponse getCollectionStrategy(Integer invoiceId);

    CustomizedReminderResponse generateCustomReminder(Integer invoiceId);

    ExecutiveSummaryResponse generateWeeklySummary();

}
