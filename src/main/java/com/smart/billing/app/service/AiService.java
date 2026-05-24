package com.smart.billing.app.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.smart.billing.app.dto.AiDTO.CollectionStrategyResponse;
import com.smart.billing.app.dto.AiDTO.CustomizedReminderResponse;
import com.smart.billing.app.dto.AiDTO.ExecutiveSummaryResponse;
import com.smart.billing.app.dto.AiDTO.RiskPredictionResponse;
import com.smart.billing.app.repository.CustomerRepository;
import com.smart.billing.app.repository.InvoiceRepository;


	@Service
	public class AiService implements IAiService {

		private final ChatClient chatClient;
		private final CustomerRepository customerRepository;
		private final InvoiceRepository invoiceRepository;
        

		public AiService(ChatClient.Builder chatClientBuilder, 
						 CustomerRepository customerRepository, 
						 InvoiceRepository invoiceRepository ) {
			this.chatClient = chatClientBuilder.build();
			this.customerRepository = customerRepository;
			this.invoiceRepository = invoiceRepository;

			System.out.println("Ruta Credenciales: " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
		}

		/**
		* Predicts the financial risk of a customer.
		*
		* Retrieves the customer's financial history from the repository and builds
		* a contextual prompt for the AI model. The AI evaluates the metrics and
		* returns a risk prediction including score, level, and justification.
		*
		* @param customerId the unique identifier of the customer
		* @return RiskPredictionResponse containing tax ID, risk score, risk level, and justification
		* @throws IllegalArgumentException if the customer is not found in the repository
		*/
		public RiskPredictionResponse predictCustomerRisk(Integer customerId) {
			Map<String, Object> context = customerRepository.findCustomerFinancialHistory(customerId)
					.orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado en el sistema"));

			String userPrompt = """
				Analyze the following customer financial metrics map. Calculate their predictive default score.
				Customer Metrics: %s
				""".formatted(context.toString());

			return this.chatClient.prompt()
					.system("You are a predictive analytics agent for SmartBilling AI. You assess risks from 0.0 to 100.0.")
					.user(userPrompt)
					.call()
					.entity(RiskPredictionResponse.class); 
		}
       


		/**
		 * Recommends a collection strategy for a specific invoice.
		 *
		 * Retrieves invoice and customer context from the repository and constructs
		 * a prompt for the AI model. The AI suggests a tactical collection action
		 * such as EMAIL_REMINDER, PAYMENT_PLAN, or EXTERNAL_COLLECTION, along with
		 * reasoning and priority level.
		 *
		 * @param invoiceId the unique identifier of the invoice
		 * @return CollectionStrategyResponse containing recommended action, priority, and tactical reasoning
		 * @throws IllegalArgumentException if the invoice is not found in the repository
		*/
		public CollectionStrategyResponse getCollectionStrategy(Integer invoiceId) {
			Map<String, Object> context = invoiceRepository.findInvoiceWithCustomerContext(invoiceId)
					.orElseThrow(() -> new IllegalArgumentException("Invoice not found in the system"));

			String userPrompt = """
				Analyze the following invoice and its customer context to suggest a tactical collection action.
				Structural data: %s
				""".formatted(context.toString());

			return this.chatClient.prompt()
					.system("Determine if it applies: EMAIL_REMINDER (low debt/low risk), PAYMENT_PLAN (high debt/medium risk), or EXTERNAL_COLLECTION (high risk/prolonged overdue).")
					.user(userPrompt)
					.call()
					.entity(CollectionStrategyResponse.class);
		}

		/**
		* Generates a customized reminder message for a given invoice.
		*
		* Retrieves invoice and customer context from the repository and builds
		* a prompt for the AI model. The AI produces personalized communication
		* templates (email subject, email body, SMS body) tailored to the customer's
		* risk profile and invoice status.
		*
		* @param invoiceId the unique identifier of the invoice
		* @return CustomizedReminderResponse containing email subject, email body, and SMS body
		* @throws IllegalArgumentException if the invoice is not found in the repository
		*/
		public CustomizedReminderResponse generateCustomReminder(Integer invoiceId) {
			Map<String, Object> context = invoiceRepository.findInvoiceWithCustomerContext(invoiceId)
					.orElseThrow(() -> new IllegalArgumentException("Factura no encontrada para procesar notificación"));

			String userPrompt = """
			   Generate a customized collection template tailored to the profile.
			   If the score is high, be strict and corporate; if it's low, be collaborative.
			   Base information: %s
			""".formatted(context.toString());

			return this.chatClient.prompt()
					.system("Generates the requested text bodies. Replaces generic variables with the actual data provided.")
					.user(userPrompt)
					.call()
					.entity(CustomizedReminderResponse.class);
		}

		/**
		 * Produces an executive summary report of weekly billing performance.
		 *
		 * Retrieves weekly performance metrics from the repository and builds
		 * a prompt for the AI model. The AI generates a concise analytical report
		 * for management, highlighting summary text, high-risk customers, and
		 * critical alerts. The method also parses raw customer strings into a list.
		 *
		 * @return ExecutiveSummaryResponse containing summary text, list of high-risk customers, and critical alerts
		*/  
		public ExecutiveSummaryResponse generateWeeklySummary() {
			Map<String, Object> metrics = invoiceRepository.getWeeklyPerformanceMetrics();

			String rawCustomers = (String) metrics.get("highRiskCustomersRaw");
			List<String> customerList = (rawCustomers != null && !rawCustomers.isBlank()) 
					? Arrays.asList(rawCustomers.split(",\\s*")) 
					: Collections.emptyList();

			String userPrompt = """
				Write a corporate summary in natural language based on these operational results from the week:
				Metrics recovered: %s
				""".formatted(metrics.toString());

			ExecutiveSummaryResponse aiResponse = this.chatClient.prompt()
					.system("Generate an analytical report for the CFO. It should be concise, clear, and have a high business impact.")
					.user(userPrompt)
					.call()
					.entity(ExecutiveSummaryResponse.class);

			return new ExecutiveSummaryResponse(
					aiResponse.summaryText(),
					customerList,
					aiResponse.criticalAlerts()
			);
		}





	}