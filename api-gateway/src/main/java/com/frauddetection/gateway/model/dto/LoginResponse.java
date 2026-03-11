package com.frauddetection.gateway.model.dto;

public record LoginResponse(String token, String tokenType, long expiresInMs) {

    public static LoginResponse bearer(String token, long expiresInMs) {
        return new LoginResponse(token, "Bearer", expiresInMs);
    }
}
