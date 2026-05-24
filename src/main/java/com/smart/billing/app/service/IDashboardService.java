package com.smart.billing.app.service;

import com.smart.billing.app.dto.DashboardResponseDTO;

/**
 * Service interface for dashboard operations in the Smart Billing application.
 *
 * This interface defines the contract for retrieving high-level financial metrics
 * and analytical data, providing a unified view of the application's financial health.
 * It supports the aggregation of billing data for management and reporting purposes.
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
public interface IDashboardService  {

    /**
     * Retrieves the aggregated dashboard summary data.
     *
     * This method fetches key financial indicators including total amounts invoiced,
     * total collections, overdue amounts, and a monthly cash flow forecast.
     *
     * @return DashboardResponseDTO containing the aggregated financial metrics and forecasts
     */
    DashboardResponseDTO getSummary();

}
