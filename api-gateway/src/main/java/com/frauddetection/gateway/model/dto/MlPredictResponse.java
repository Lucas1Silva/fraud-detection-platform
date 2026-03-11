package com.frauddetection.gateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MlPredictResponse {

    @JsonProperty("fraud_score")
    private double fraudScore;

    @JsonProperty("is_fraud")
    private boolean isFraud;

    @JsonProperty("model_version")
    private String modelVersion;
}
