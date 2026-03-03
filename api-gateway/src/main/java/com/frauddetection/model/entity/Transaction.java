package com.frauddetection.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Transaction entity — schema defined in Sprint 1 migration.
 * Placeholder to establish the package structure.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String cardLast4;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public UUID getId() { return id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }

    public Instant getCreatedAt() { return createdAt; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public enum TransactionStatus {
        PENDING, APPROVED, FLAGGED, REJECTED
    }
}
