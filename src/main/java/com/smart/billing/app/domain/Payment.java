package com.smart.billing.app.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @CreationTimestamp
    @Column(name = "payment_date", updatable = false)
    private LocalDateTime paymentDate;

    @Column(name = "amount_paid", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", updatable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() { 
        this.createdAt = LocalDateTime.now(); 
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
         this.updatedAt = LocalDateTime.now();
    }



}
