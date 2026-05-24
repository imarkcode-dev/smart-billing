package com.smart.billing.app.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.billing.app.domain.Invoice;

@Repository
public interface InvoiceRepository  extends JpaRepository<Invoice, Integer> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Query(value = """
        SELECT i.invoice_number as invoiceNumber, i.due_date as dueDate, i.total_amount as totalAmount, 
               i.status as invoiceStatus, i.penalty_amount as penaltyAmount,
               c.name_customer as nameCustomer, c.email as customerEmail, c.risk_score as customerRiskScore
        FROM invoice i
        JOIN contract con ON i.contract_id = con.id
        JOIN customer c ON con.customer_id = c.id
        WHERE i.id = :invoiceId
    """, nativeQuery = true)
    Optional<Map<String, Object>> findInvoiceWithCustomerContext(@Param("invoiceId") Integer invoiceId);

    @Query(value = """
        SELECT 
            COALESCE(SUM(p.amount_paid), 0) as recoveredThisWeek,
            COUNT(CASE WHEN i.status = 'OVERDUE' THEN 1 END) as activeOverdueCount,
            COALESCE(SUM(CASE WHEN i.status = 'OVERDUE' THEN i.total_amount ELSE 0 END), 0) as volumeAtRisk,
            (SELECT STRING_AGG(name_customer, ', ') FROM customer WHERE risk_score > 60) as highRiskCustomersRaw
        FROM invoice i
        LEFT JOIN payment p ON i.id = p.invoice_id
        WHERE i.created_at >= NOW() - INTERVAL '7 days'
    """, nativeQuery = true)
    Map<String, Object> getWeeklyPerformanceMetrics();

}
