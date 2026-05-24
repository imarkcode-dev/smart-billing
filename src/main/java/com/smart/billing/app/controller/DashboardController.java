package com.smart.billing.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.billing.app.service.IDashboardService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing dashboard-related data in the Smart Billing application.
 *
 * This controller provides endpoints for retrieving aggregated financial metrics
 * and analytical data to populate the application's dashboard. It serves as the
 * entry point for dashboard-specific operations in the REST API.
 *
 * All endpoints are accessible via the base path "/api/v1/dashboard" and support
 * cross-origin requests from any origin for flexibility in frontend integration.
 *
 * Key features:
 * - Retrieval of summary financial metrics (total invoiced, collected, overdue, cash flow forecast).
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    /**
     * Retrieves the dashboard summary data including total invoiced, 
     * collected amounts, overdue amounts, and monthly cash flow forecast.
     * 
     * @return ResponseEntity containing DashboardResponseDTO with summary metrics
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }


}
