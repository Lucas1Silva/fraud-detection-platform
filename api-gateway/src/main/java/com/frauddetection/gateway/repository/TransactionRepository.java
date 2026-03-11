package com.frauddetection.gateway.repository;

import com.frauddetection.gateway.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByIsFraudTrue();

    List<Transaction> findByAmountGreaterThan(BigDecimal threshold);

    List<Transaction> findByTransactionTimestampBetween(LocalDateTime start, LocalDateTime end);

    /** Returns transactions flagged as fraud OR scored above the high-risk threshold. */
    @Query("SELECT t FROM Transaction t WHERE t.isFraud = true OR t.fraudScore > 0.7")
    List<Transaction> findFlagged();
}
