package com.frauddetection.gateway.service;

import com.frauddetection.gateway.model.dto.MlPredictRequest;
import com.frauddetection.gateway.model.dto.MlPredictResponse;
import com.frauddetection.gateway.model.dto.TransactionRequest;
import com.frauddetection.gateway.model.dto.TransactionResponse;
import com.frauddetection.gateway.model.entity.Transaction;
import com.frauddetection.gateway.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;
    private final MLServiceClient mlServiceClient;

    public TransactionService(TransactionRepository repository, MLServiceClient mlServiceClient) {
        this.repository = repository;
        this.mlServiceClient = mlServiceClient;
    }

    /**
     * Persists a new transaction, calls the ML service for fraud scoring, and returns the result.
     */
    public TransactionResponse create(TransactionRequest req) {
        Transaction transaction = new Transaction();
        transaction.setAmount(req.getAmount());
        transaction.setMerchantName(req.getMerchantName());
        transaction.setMerchantCategory(req.getMerchantCategory());
        transaction.setCardNumberHash(req.getCardNumberHash());
        transaction.setTransactionTimestamp(req.getTransactionTimestamp());
        transaction.setLatitude(req.getLatitude());
        transaction.setLongitude(req.getLongitude());

        // Persist first to get the UUID assigned
        transaction = repository.save(transaction);
        log.info("Transaction {} persisted, calling ML service for scoring", transaction.getId());

        // Call ML service — MLServiceUnavailableException propagates and is handled by GlobalExceptionHandler
        MlPredictRequest mlRequest = buildMlRequest(transaction);
        MlPredictResponse mlResponse = mlServiceClient.predict(mlRequest);

        // Populate fraud score and flag
        transaction.setFraudScore(mlResponse.getFraudScore());
        transaction.setIsFraud(mlResponse.getFraudScore() > 0.7);
        transaction = repository.save(transaction);

        log.info("Transaction {} scored: fraudScore={} isFraud={}",
                transaction.getId(), transaction.getFraudScore(), transaction.getIsFraud());
        return TransactionResponse.from(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(TransactionResponse::from);
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(UUID id) {
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));
        return TransactionResponse.from(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findFlagged() {
        return repository.findFlagged().stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    private MlPredictRequest buildMlRequest(Transaction t) {
        int hour = t.getTransactionTimestamp() != null
                ? t.getTransactionTimestamp().getHour()
                : 0;
        int dayOfWeek = t.getTransactionTimestamp() != null
                ? t.getTransactionTimestamp().getDayOfWeek().getValue()
                : 1;

        return MlPredictRequest.builder()
                .amount(t.getAmount().doubleValue())
                .merchantCategory(t.getMerchantCategory())
                .hourOfDay(hour)
                .dayOfWeek(dayOfWeek)
                .latitude(t.getLatitude() != null ? t.getLatitude() : 0.0)
                .longitude(t.getLongitude() != null ? t.getLongitude() : 0.0)
                .build();
    }
}
