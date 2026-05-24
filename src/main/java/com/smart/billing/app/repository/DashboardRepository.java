package com.smart.billing.app.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smart.billing.app.domain.Invoice;

@Repository
public interface DashboardRepository extends JpaRepository<Invoice, Long> {

    // 1. Total Invoiced: Sum of total_amount of uncancelled invoices
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status != 'CANCELLED'")
    BigDecimal getTotalInvoiced();

    // 2. Payments Received: Sum of amount_paid from the payment table
    // Note: We use a query on the Payment entity even though the repository is for Invoice
    @Query("SELECT SUM(p.amountPaid) FROM Payment p")
    BigDecimal getTotalCollected();

   // 3. Overdue Debt: Sum of total_amount where the due date has passed and it remains unpaid.
   // Since there is no 'balance' column, we calculate based on the total of outstanding amounts.
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.dueDate < CURRENT_TIMESTAMP AND i.status = 'PENDING'")
    BigDecimal getOverdueAmount();

    // Cash Flow: Grouped by month (Native Query for PostgreSQL)
    @Query(value = """
        SELECT to_char(payment_date, 'YYYY-MM') as month, SUM(amount_paid) 
        FROM payment 
        WHERE payment_date > CURRENT_DATE - INTERVAL '6 months'
        GROUP BY 1 ORDER BY 1
        """, nativeQuery = true)
    List<Object[]> getMonthlyCashFlow();

}
