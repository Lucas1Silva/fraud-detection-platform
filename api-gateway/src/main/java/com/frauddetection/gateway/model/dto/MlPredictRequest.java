package com.frauddetection.gateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MlPredictRequest {

    private double amount;

    @JsonProperty("merchant_category")
    private String merchantCategory;

    @JsonProperty("hour_of_day")
    private int hourOfDay;

    @JsonProperty("day_of_week")
    private int dayOfWeek;

    private double latitude;

    private double longitude;
}
