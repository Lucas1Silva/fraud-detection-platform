package com.frauddetection.controller;

import com.frauddetection.model.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Health endpoint — kept from Sprint 0 for backward compatibility.
 * Transaction endpoints live in {@link com.frauddetection.gateway.controller.TransactionController}.
 */
@RestController
@RequestMapping("/api")
public class TransactionController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse(
                "UP",
                "fraud-detection-api-gateway",
                "0.1.0",
                Instant.now().toString()
        ));
    }
}
