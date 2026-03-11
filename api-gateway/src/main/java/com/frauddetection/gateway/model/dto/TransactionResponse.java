package com.frauddetection.gateway.model.dto;

import com.frauddetection.gateway.model.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private UUID id;
    private BigDecimal amount;
    private String merchantName;
    private String merchantCategory;
    private String cardNumberHash;
    private LocalDateTime transactionTimestamp;
    private Double latitude;
    private Double longitude;
    private Boolean isFraud;
    private Double fraudScore;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.id = t.getId();
        r.amount = t.getAmount();
        r.merchantName = t.getMerchantName();
        r.merchantCategory = t.getMerchantCategory();
        r.cardNumberHash = t.getCardNumberHash();
        r.transactionTimestamp = t.getTransactionTimestamp();
        r.latitude = t.getLatitude();
        r.longitude = t.getLongitude();
        r.isFraud = t.getIsFraud();
        r.fraudScore = t.getFraudScore();
        r.createdAt = t.getCreatedAt();
        return r;
    }
}
