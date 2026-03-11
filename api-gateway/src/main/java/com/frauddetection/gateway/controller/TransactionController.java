package com.frauddetection.gateway.controller;

import com.frauddetection.gateway.model.dto.TransactionRequest;
import com.frauddetection.gateway.model.dto.TransactionResponse;
import com.frauddetection.gateway.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * POST /api/transactions
     * Creates a transaction, calls the ML service for scoring, and returns the persisted result.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/transactions?page=0&size=20&sort=createdAt,desc
     * Lists all transactions with Spring Data pagination.
     */
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(transactionService.findAll(pageable));
    }

    /**
     * GET /api/transactions/{id}
     * Returns a single transaction by UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    /**
     * GET /api/transactions/flagged
     * Returns transactions with isFraud=true OR fraudScore > 0.7.
     */
    @GetMapping("/flagged")
    public ResponseEntity<List<TransactionResponse>> findFlagged() {
        return ResponseEntity.ok(transactionService.findFlagged());
    }
}
