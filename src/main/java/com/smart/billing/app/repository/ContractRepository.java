package com.smart.billing.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.billing.app.domain.Contract;



@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

     List<Contract> findByCustomerId(Integer customerId);

     List<Contract> findByStatus(String status);
}

