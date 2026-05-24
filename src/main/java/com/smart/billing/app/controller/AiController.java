package com.smart.billing.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.billing.app.dto.AiDTO.CollectionStrategyResponse;
import com.smart.billing.app.dto.AiDTO.CustomizedReminderResponse;
import com.smart.billing.app.dto.AiDTO.ExecutiveSummaryResponse;
import com.smart.billing.app.dto.AiDTO.RiskPredictionResponse;
import com.smart.billing.app.service.IAiService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller that exposes AI-powered endpoints for Smart Billing.
 *
 * This controller provides access to analytical and generative AI features
 * related to customer risk assessment, collection strategies, personalized
 * reminders, and executive summaries. It delegates all business logic to
 * the {@link IAiService} service layer.
 *
 * Endpoints:
 * - GET /api/v1/ia/predict-risk/{customerId}:
 *   Predicts the financial risk level of a customer based on historical metrics.
 *
 * - GET /api/v1/ia/collection-strategy/{invoiceId}:
 *   Suggests an appropriate collection strategy for a given invoice and customer context.
 *
 * - GET /api/v1/ia/generate-reminder/{invoiceId}:
 *   Generates a customized reminder message (email/SMS) tailored to the customer profile.
 *
 * - GET /api/v1/ia/weekly-summary:
 *   Produces an executive summary report of weekly billing performance and risk alerts.
 *
 * Cross-Origin Resource Sharing (CORS) is enabled for all origins to allow
 * external clients to consume these endpoints.
 *
 * The controller returns responses wrapped in {@link ResponseEntity} objects,
 * ensuring proper HTTP status codes and JSON serialization of DTOs.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/ia")
@RequiredArgsConstructor
public class AiController {

    private final IAiService aiService;


    /**
     * Predicts the financial risk level of a customer.
     *
     * This endpoint analyzes the customer's financial history and returns
     * a risk prediction including score, level, and justification.
     *
     * @param customerId the unique identifier of the customer
     * @return ResponseEntity containing a RiskPredictionResponse with risk details
     */
    @GetMapping("/predict-risk/{customerId}")
    public ResponseEntity<RiskPredictionResponse> predictCustomerRisk(@PathVariable Integer customerId) {
        return ResponseEntity.ok(aiService.predictCustomerRisk(customerId));
    }

    /**
     * Suggests a collection strategy for a given invoice.
     *
     * This endpoint evaluates the invoice and customer context to recommend
     * an appropriate collection action such as EMAIL_REMINDER, PAYMENT_PLAN,
     * or EXTERNAL_COLLECTION.
     *
     * @param invoiceId the unique identifier of the invoice
     * @return ResponseEntity containing a CollectionStrategyResponse with strategy details
     */
    @GetMapping("/collection-strategy/{invoiceId}")
    public ResponseEntity<CollectionStrategyResponse> getCollectionStrategy(@PathVariable Integer invoiceId) {
        return ResponseEntity.ok(aiService.getCollectionStrategy(invoiceId));
    }

    /**
     * Generates a customized reminder message for a given invoice.
     *
     * This endpoint produces personalized communication templates (email subject,
     * email body, SMS body) tailored to the customer's risk profile and invoice status.
     *
     * @param invoiceId the unique identifier of the invoice
     * @return ResponseEntity containing a CustomizedReminderResponse with reminder content
     */
    @GetMapping("/generate-reminder/{invoiceId}")
    public ResponseEntity<CustomizedReminderResponse> generateCustomReminder(@PathVariable Integer invoiceId) {
        return ResponseEntity.ok(aiService.generateCustomReminder(invoiceId));
    }


    /**
     * Produces an executive summary report of weekly billing performance.
     *
     * This endpoint generates a concise analytical summary for management,
     * highlighting key metrics, high-risk customers, and critical alerts.
     *
     * @return ResponseEntity containing an ExecutiveSummaryResponse with summary details
     */
    @GetMapping("/weekly-summary")
    public ResponseEntity<ExecutiveSummaryResponse> getWeeklySummary() {
        return ResponseEntity.ok(aiService.generateWeeklySummary());
    }


}
