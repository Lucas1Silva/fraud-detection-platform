package com.frauddetection.gateway.service;

import com.frauddetection.gateway.exception.MLServiceUnavailableException;
import com.frauddetection.gateway.model.dto.MlPredictRequest;
import com.frauddetection.gateway.model.dto.MlPredictResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class MLServiceClient {

    private static final Logger log = LoggerFactory.getLogger(MLServiceClient.class);

    private final RestTemplate restTemplate;
    private final String mlServiceBaseUrl;

    public MLServiceClient(
            RestTemplate restTemplate,
            @Value("${ml-service.base-url}") String mlServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.mlServiceBaseUrl = mlServiceBaseUrl;
    }

    /**
     * Calls the ML service to score a transaction.
     *
     * @throws MLServiceUnavailableException when the ML service is unreachable or returns an error.
     */
    public MlPredictResponse predict(MlPredictRequest request) {
        String url = mlServiceBaseUrl + "/predict";
        log.debug("Calling ML service at {} with amount={} category={}",
                url, request.getAmount(), request.getMerchantCategory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MlPredictRequest> entity = new HttpEntity<>(request, headers);

        try {
            MlPredictResponse response = restTemplate.postForObject(url, entity, MlPredictResponse.class);
            if (response == null) {
                throw new MLServiceUnavailableException("ML service returned empty response");
            }
            log.debug("ML service scored transaction: fraudScore={} isFraud={}",
                    response.getFraudScore(), response.isFraud());
            return response;
        } catch (ResourceAccessException e) {
            log.error("ML service is unreachable at {}: {}", url, e.getMessage());
            throw new MLServiceUnavailableException("ML service is currently unavailable: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error calling ML service: {}", e.getMessage());
            throw new MLServiceUnavailableException("Error communicating with ML service: " + e.getMessage());
        }
    }
}
