package com.smart.billing.app.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.smart.billing.app.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    Optional<Customer> findByTaxId(String taxId);
    
    boolean existsByTaxId(String taxId);

    @Query(value = """
        SELECT c.tax_id as taxId, c.name_customer as nameCustomer, c.risk_score as currentRiskScore, 
               COUNT(i.id) as totalInvoices,
               COUNT(CASE WHEN i.status = 'OVERDUE' THEN 1 END) as overdueCount,
               COALESCE(SUM(CASE WHEN i.status = 'OVERDUE' THEN i.total_amount ELSE 0 END), 0) as totalOverdueAmount,
               COALESCE(SUM(p.amount_paid), 0) as historicalPayments
        FROM customer c
        LEFT JOIN contract con ON c.id = con.customer_id
        LEFT JOIN invoice i ON con.id = i.contract_id
        LEFT JOIN payment p ON i.id = p.invoice_id
        WHERE c.id = :customerId
        GROUP BY c.id, c.tax_id, c.name_customer, c.risk_score
    """, nativeQuery = true)
    Optional<Map<String, Object>> findCustomerFinancialHistory(@Param("customerId") Integer customerId);

}
