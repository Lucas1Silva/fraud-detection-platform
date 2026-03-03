package com.frauddetection.controller;

import com.frauddetection.model.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * TransactionController — Sprint 0 skeleton.
 * Full transaction ingestion endpoints are wired in Sprint 1.
 */
@RestController
@RequestMapping("/api")
public class TransactionController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = new HealthResponse(
                "UP",
                "fraud-detection-api-gateway",
                "0.1.0",
                Instant.now().toString()
        );
        return ResponseEntity.ok(response);
    }
}
