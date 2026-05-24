/*
Generate unit tests for DashboardController using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock IDashboardService
- Test all methods:
    - getSummary
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
import java.util.Map;

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

import com.smart.billing.app.dto.DashboardResponseDTO;
import com.smart.billing.app.exception.GlobalExceptionHandler;
import com.smart.billing.app.service.IDashboardService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardController Unit Tests")
public class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IDashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private DashboardResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new DashboardResponseDTO(
                new BigDecimal("50000.00"),
                new BigDecimal("35000.00"),
                new BigDecimal("15000.00"),
                Map.of("Jan", new BigDecimal("10000.00"), "Feb", new BigDecimal("12000.00"))
        );
    }

    @Test
    @DisplayName("GET /summary should return 200 OK and dashboard data")
    void getSummary_ShouldReturnDashboardSummary_WhenSuccess() throws Exception {
        // Given
        when(dashboardService.getSummary()).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/dashboard/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalInvoiced").value(50000.00))
                .andExpect(jsonPath("$.totalCollected").value(35000.00))
                .andExpect(jsonPath("$.overdueAmount").value(15000.00))
                .andExpect(jsonPath("$.cashFlowForecast.Jan").value(10000.00))
                .andExpect(jsonPath("$.cashFlowForecast.Feb").value(12000.00));

        // Verify service interaction
        verify(dashboardService, times(1)).getSummary();
        verifyNoMoreInteractions(dashboardService);
    }

    @Test
    @DisplayName("GET /summary should return 500 Internal Server Error when service fails")
    void getSummary_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(dashboardService.getSummary()).thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(get("/api/v1/dashboard/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        // Verify service interaction
        verify(dashboardService, times(1)).getSummary();
        verifyNoMoreInteractions(dashboardService);
    }
}
