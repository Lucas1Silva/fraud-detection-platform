package com.frauddetection.gateway.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** Transaction amount — BigDecimal for financial precision (never Double). */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String merchantName;

    @Column(nullable = false)
    private String merchantCategory;

    /** SHA-256 hash of the card number — never store the raw PAN. */
    @Column(nullable = false)
    private String cardNumberHash;

    @Column(nullable = false)
    private LocalDateTime transactionTimestamp;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Boolean isFraud = false;

    /** Populated by the ML service after scoring. Null until scored. */
    private Double fraudScore;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.transactionTimestamp == null) {
            this.transactionTimestamp = LocalDateTime.now();
        }
        if (this.isFraud == null) {
            this.isFraud = false;
        }
    }
}
