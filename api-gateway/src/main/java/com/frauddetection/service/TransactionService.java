package com.frauddetection.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * TransactionService — Sprint 0 stub.
 * Business logic for transaction ingestion and fraud orchestration
 * will be implemented in Sprint 1.
 */
@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService() {
        log.info("TransactionService initialized (Sprint 0 stub)");
    }
}
