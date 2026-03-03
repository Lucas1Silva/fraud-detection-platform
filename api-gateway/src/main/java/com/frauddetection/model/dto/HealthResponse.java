package com.frauddetection.model.dto;

/**
 * DTO returned by the /api/health endpoint.
 */
public record HealthResponse(
        String status,
        String service,
        String version,
        String timestamp
) {}
