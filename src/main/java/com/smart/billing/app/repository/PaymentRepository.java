package com.smart.billing.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smart.billing.app.domain.Payment;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("SELECT p FROM Payment p JOIN FETCH p.invoice")
    List<Payment> findAllInvoiceWithInvoice();

}
