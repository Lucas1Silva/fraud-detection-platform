package com.frauddetection.gateway.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequest {

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be positive")
    @Digits(integer = 13, fraction = 2, message = "amount must have at most 13 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotBlank(message = "merchantName is required")
    private String merchantName;

    @NotBlank(message = "merchantCategory is required")
    private String merchantCategory;

    @NotBlank(message = "cardNumberHash is required")
    private String cardNumberHash;

    private LocalDateTime transactionTimestamp;

    private Double latitude;

    private Double longitude;
}
