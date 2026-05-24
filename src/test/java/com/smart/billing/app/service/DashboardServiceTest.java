package com.smart.billing.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.billing.app.dto.DashboardResponseDTO;
import com.smart.billing.app.repository.DashboardRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService Unit Tests")
public class DashboardServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private BigDecimal totalInvoiced;
    private BigDecimal totalCollected;
    private BigDecimal overdueAmount;
    private List<Object[]> monthlyCashFlowData;

    @BeforeEach
    void setUp() {
        totalInvoiced = new BigDecimal("15000.75");
        totalCollected = new BigDecimal("10000.50");
        overdueAmount = new BigDecimal("2500.25");
        monthlyCashFlowData = Arrays.asList(
            new Object[]{"Jan", new BigDecimal("5000.00")},
            new Object[]{"Feb", new BigDecimal("6000.00")}
        );
    }

    @Test
    @DisplayName("getSummary should return DashboardResponseDTO with all data present")
    void getSummary_AllDataPresent_ReturnsDTO() {
        // Given
        when(dashboardRepository.getTotalInvoiced()).thenReturn(totalInvoiced);
        when(dashboardRepository.getTotalCollected()).thenReturn(totalCollected);
        when(dashboardRepository.getOverdueAmount()).thenReturn(overdueAmount);
        when(dashboardRepository.getMonthlyCashFlow()).thenReturn(monthlyCashFlowData);

        // When
        DashboardResponseDTO result = dashboardService.getSummary();

        // Then
        assertNotNull(result);
        assertEquals(totalInvoiced, result.totalInvoiced());
        assertEquals(totalCollected, result.totalCollected());
        assertEquals(overdueAmount, result.overdueAmount());
        assertNotNull(result.cashFlowForecast());
        assertEquals(2, result.cashFlowForecast().size());
        assertEquals(new BigDecimal("5000.00"), result.cashFlowForecast().get("Jan"));
        assertEquals(new BigDecimal("6000.00"), result.cashFlowForecast().get("Feb"));

        verify(dashboardRepository, times(1)).getTotalInvoiced();
        verify(dashboardRepository, times(1)).getTotalCollected();
        verify(dashboardRepository, times(1)).getOverdueAmount();
        verify(dashboardRepository, times(1)).getMonthlyCashFlow();
    }

    @Test
    @DisplayName("getSummary should handle null BigDecimals from repository and default to ZERO")
    void getSummary_NullBigDecimals_DefaultsToZero() {
        // Given
        when(dashboardRepository.getTotalInvoiced()).thenReturn(null);
        when(dashboardRepository.getTotalCollected()).thenReturn(null);
        when(dashboardRepository.getOverdueAmount()).thenReturn(null);
        when(dashboardRepository.getMonthlyCashFlow()).thenReturn(Collections.emptyList()); // Empty forecast

        // When
        DashboardResponseDTO result = dashboardService.getSummary();

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.totalInvoiced());
        assertEquals(BigDecimal.ZERO, result.totalCollected());
        assertEquals(BigDecimal.ZERO, result.overdueAmount());
        assertTrue(result.cashFlowForecast().isEmpty());

        verify(dashboardRepository, times(1)).getTotalInvoiced();
        verify(dashboardRepository, times(1)).getTotalCollected();
        verify(dashboardRepository, times(1)).getOverdueAmount();
        verify(dashboardRepository, times(1)).getMonthlyCashFlow();
    }

    @Test
    @DisplayName("getSummary should return an empty cashFlowForecast map if repository returns empty list")
    void getSummary_EmptyCashFlowList_ReturnsEmptyMap() {
        // Given
        when(dashboardRepository.getTotalInvoiced()).thenReturn(totalInvoiced);
        when(dashboardRepository.getTotalCollected()).thenReturn(totalCollected);
        when(dashboardRepository.getOverdueAmount()).thenReturn(overdueAmount);
        when(dashboardRepository.getMonthlyCashFlow()).thenReturn(Collections.emptyList());

        // When
        DashboardResponseDTO result = dashboardService.getSummary();

        // Then
        assertNotNull(result);
        assertEquals(totalInvoiced, result.totalInvoiced());
        assertEquals(totalCollected, result.totalCollected());
        assertEquals(overdueAmount, result.overdueAmount());
        assertNotNull(result.cashFlowForecast());
        assertTrue(result.cashFlowForecast().isEmpty());

        verify(dashboardRepository, times(1)).getTotalInvoiced();
        verify(dashboardRepository, times(1)).getTotalCollected();
        verify(dashboardRepository, times(1)).getOverdueAmount();
        verify(dashboardRepository, times(1)).getMonthlyCashFlow();
    }

    @Test
    @DisplayName("getSummary should correctly map cash flow data with mixed types")
    void getSummary_MixedCashFlowData_MapsCorrectly() {
        // Given
        List<Object[]> mixedCashFlowData = Arrays.asList(
            new Object[]{"Mar", new BigDecimal("7500.00")},
            new Object[]{"Apr", new BigDecimal("8200.00")}
        );
        when(dashboardRepository.getTotalInvoiced()).thenReturn(totalInvoiced);
        when(dashboardRepository.getTotalCollected()).thenReturn(totalCollected);
        when(dashboardRepository.getOverdueAmount()).thenReturn(overdueAmount);
        when(dashboardRepository.getMonthlyCashFlow()).thenReturn(mixedCashFlowData);

        // When
        DashboardResponseDTO result = dashboardService.getSummary();

        // Then
        assertNotNull(result);
        assertNotNull(result.cashFlowForecast());
        assertEquals(2, result.cashFlowForecast().size());
        assertEquals(new BigDecimal("7500.00"), result.cashFlowForecast().get("Mar"));
        assertEquals(new BigDecimal("8200.00"), result.cashFlowForecast().get("Apr"));

        verify(dashboardRepository, times(1)).getMonthlyCashFlow();
    }
}