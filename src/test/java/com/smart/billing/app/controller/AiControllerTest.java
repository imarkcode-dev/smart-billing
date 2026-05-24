/*
Generate unit tests for AiController using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock IAiService
- Test all methods:
    - predictCustomerRisk
    - getCollectionStrategy
    - generateCustomReminder
    - getWeeklySummary
- Use given-when-then structure
- Verify repository interactions
- Use assertThrows for exceptions
- Achieve 100% coverage
- Do not use SpringBootTest
*/

package com.smart.billing.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.smart.billing.app.dto.AiDTO.CollectionStrategyResponse;
import com.smart.billing.app.dto.AiDTO.CustomizedReminderResponse;
import com.smart.billing.app.dto.AiDTO.ExecutiveSummaryResponse;
import com.smart.billing.app.dto.AiDTO.RiskPredictionResponse;
import com.smart.billing.app.exception.GlobalExceptionHandler;
import com.smart.billing.app.service.IAiService;

/**
 * Unit tests for AiController.
 * Validates REST endpoints for AI-powered features including risk analysis,
 * collection strategies, and executive reporting.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AiController Unit Tests")
public class AiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAiService aiService;

    @InjectMocks
    private AiController aiController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /predict-risk/{customerId} should return prediction when successful")
    void predictCustomerRisk_Success() throws Exception {
        // Given
        Integer customerId = 123;
        RiskPredictionResponse response = new RiskPredictionResponse(
            "TAX-123", BigDecimal.valueOf(25.5), "MEDIUM", "Reasoning info"
        );
        when(aiService.predictCustomerRisk(customerId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/ia/predict-risk/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxId").value("TAX-123"))
                .andExpect(jsonPath("$.riskScore").value(25.5))
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"));

        verify(aiService, times(1)).predictCustomerRisk(customerId);
    }

    @Test
    @DisplayName("GET /predict-risk/{customerId} should return 400 when client not found")
    void predictCustomerRisk_NotFound() throws Exception {
        // Given
        Integer customerId = 999;
        when(aiService.predictCustomerRisk(customerId))
                .thenThrow(new IllegalArgumentException("Cliente no encontrado en el sistema"));

        // When & Then
        mockMvc.perform(get("/api/v1/ia/predict-risk/{customerId}", customerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado en el sistema"));
    }

    @Test
    @DisplayName("GET /collection-strategy/{invoiceId} should return strategy details")
    void getCollectionStrategy_Success() throws Exception {
        // Given
        Integer invoiceId = 456;
        CollectionStrategyResponse response = new CollectionStrategyResponse(
            "PAYMENT_PLAN", "HIGH", "Reasoning for payment plan"
        );
        when(aiService.getCollectionStrategy(invoiceId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/ia/collection-strategy/{invoiceId}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedAction").value("PAYMENT_PLAN"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(aiService).getCollectionStrategy(invoiceId);
    }

    @Test
    @DisplayName("GET /generate-reminder/{invoiceId} should return customized text")
    void generateCustomReminder_Success() throws Exception {
        // Given
        Integer invoiceId = 789;
        CustomizedReminderResponse response = new CustomizedReminderResponse(
            "Reminder Subject", "Body content", "SMS content"
        );
        when(aiService.generateCustomReminder(invoiceId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/ia/generate-reminder/{invoiceId}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailSubject").value("Reminder Subject"))
                .andExpect(jsonPath("$.emailBody").value("Body content"));

        verify(aiService).generateCustomReminder(invoiceId);
    }

    @Test
    @DisplayName("GET /weekly-summary should return analytical report")
    void getWeeklySummary_Success() throws Exception {
        // Given
        ExecutiveSummaryResponse response = new ExecutiveSummaryResponse(
            "Executive summary text",
            List.of("ID-101", "ID-102"),
            "Low liquidity alerts"
        );
        when(aiService.generateWeeklySummary()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/ia/weekly-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summaryText").value("Executive summary text"))
                .andExpect(jsonPath("$.highRiskCustomers").isArray())
                .andExpect(jsonPath("$.highRiskCustomers[0]").value("ID-101"))
                .andExpect(jsonPath("$.criticalAlerts").value("Low liquidity alerts"));

        verify(aiService).generateWeeklySummary();
    }

    @Test
    @DisplayName("GET /weekly-summary should return 500 when unexpected service error occurs")
    void getWeeklySummary_InternalError() throws Exception {
        // Given
        when(aiService.generateWeeklySummary()).thenThrow(new RuntimeException("AI provider unavailable"));

        // When & Then
        mockMvc.perform(get("/api/v1/ia/weekly-summary"))
                .andExpect(status().isInternalServerError());
    }
}
