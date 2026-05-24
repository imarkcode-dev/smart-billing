/*
Generate unit tests for AiService using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock CustomerRepository
- Mock InvoiceRepository
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

package com.smart.billing.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import com.smart.billing.app.dto.AiDTO.CollectionStrategyResponse;
import com.smart.billing.app.dto.AiDTO.CustomizedReminderResponse;
import com.smart.billing.app.dto.AiDTO.ExecutiveSummaryResponse;
import com.smart.billing.app.dto.AiDTO.RiskPredictionResponse;
import com.smart.billing.app.repository.CustomerRepository;
import com.smart.billing.app.repository.InvoiceRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiService Unit Tests")
public class AiServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        aiService = new AiService(chatClientBuilder, customerRepository, invoiceRepository);
    }

    @Test
    @DisplayName("predictCustomerRisk should return prediction when customer metrics exist")
    void predictCustomerRisk_Success() {
        Integer customerId = 123;
        Map<String, Object> context = Map.of("balance", 5000.0, "delinquencyDays", 15);
        RiskPredictionResponse expected = new RiskPredictionResponse(
            "RFC123456",
            BigDecimal.valueOf(15.5),
            "LOW",
            "Cliente con historial de pagos aceptable"
        );

        when(customerRepository.findCustomerFinancialHistory(customerId)).thenReturn(Optional.of(context));
        setupChatClientMock(RiskPredictionResponse.class, expected);

        RiskPredictionResponse result = aiService.predictCustomerRisk(customerId);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(15.5), result.riskScore());
        assertEquals("LOW", result.riskLevel());
        verify(customerRepository).findCustomerFinancialHistory(customerId);
        verify(chatClient).prompt();
    }

    @Test
    @DisplayName("predictCustomerRisk should throw exception when customer context is missing")
    void predictCustomerRisk_NotFound() {
        when(customerRepository.findCustomerFinancialHistory(anyInt())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> aiService.predictCustomerRisk(1));
        assertEquals("Cliente no encontrado en el sistema", ex.getMessage());
        verifyNoInteractions(chatClient);
    }

    @Test
    @DisplayName("getCollectionStrategy should return strategy for valid invoice")
    void getCollectionStrategy_Success() {
        Integer invoiceId = 456;
        Map<String, Object> context = Map.of("amount", 1200.0);
        CollectionStrategyResponse expected = new CollectionStrategyResponse(
            "EMAIL_REMINDER",
            "HIGH",
            "Cliente con deuda baja y riesgo bajo"
        );

        when(invoiceRepository.findInvoiceWithCustomerContext(invoiceId)).thenReturn(Optional.of(context));
        setupChatClientMock(CollectionStrategyResponse.class, expected);

        CollectionStrategyResponse result = aiService.getCollectionStrategy(invoiceId);

        assertNotNull(result);
        assertEquals("EMAIL_REMINDER", result.recommendedAction());
        assertEquals("HIGH", result.priority());
        verify(invoiceRepository).findInvoiceWithCustomerContext(invoiceId);
    }

    @Test
    @DisplayName("generateCustomReminder should return response for valid invoice")
    void generateCustomReminder_Success() {
        Integer invoiceId = 789;
        Map<String, Object> context = Map.of("customer", "John Doe");
        CustomizedReminderResponse expected = new CustomizedReminderResponse(
            "Factura pendiente",
            "Estimado cliente, su factura está vencida.",
            "Su factura #202 está pendiente de pago."
        );

        when(invoiceRepository.findInvoiceWithCustomerContext(invoiceId)).thenReturn(Optional.of(context));
        setupChatClientMock(CustomizedReminderResponse.class, expected);

        CustomizedReminderResponse result = aiService.generateCustomReminder(invoiceId);

        assertNotNull(result);
        assertEquals("Factura pendiente", result.emailSubject());
        assertEquals("Estimado cliente, su factura está vencida.", result.emailBody());
        verify(invoiceRepository).findInvoiceWithCustomerContext(invoiceId);
    }

    @Test
    @DisplayName("generateWeeklySummary should process metrics and raw high-risk string")
    void generateWeeklySummary_Success() {
        Map<String, Object> metrics = Map.of(
            "totalRevenue", 100000,
            "highRiskCustomersRaw", "ID-1, ID-2, ID-3"
        );
        ExecutiveSummaryResponse aiResponse = new ExecutiveSummaryResponse(
            "Resumen semanal estable",
            Collections.emptyList(),
            "Alerta de liquidez baja"
        );

        when(invoiceRepository.getWeeklyPerformanceMetrics()).thenReturn(metrics);
        setupChatClientMock(ExecutiveSummaryResponse.class, aiResponse);

        ExecutiveSummaryResponse result = aiService.generateWeeklySummary();

        assertNotNull(result);
        assertEquals("Resumen semanal estable", result.summaryText());
        assertEquals(3, result.highRiskCustomers().size());
        assertTrue(result.highRiskCustomers().contains("ID-1"));
        assertEquals("Alerta de liquidez baja", result.criticalAlerts());
        verify(invoiceRepository).getWeeklyPerformanceMetrics();
    }

    private <T> void setupChatClientMock(Class<T> responseClass, T responseInstance) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.entity(responseClass)).thenReturn(responseInstance);
    }
}
