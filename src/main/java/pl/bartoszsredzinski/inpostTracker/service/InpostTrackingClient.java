package pl.bartoszsredzinski.inpostTracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.bartoszsredzinski.inpostTracker.dto.ParcelTrackingResponse;

@Service
public class InpostTrackingClient{

    private final RestClient restClient;

    public InpostTrackingClient(@Value("${inpost.api.base-url}") String baseUrl){
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ParcelTrackingResponse getTracking(String trackingNumber){
        return restClient.get()
                .uri("/v1/tracking/{trackingNumber}", trackingNumber)
                .retrieve()
                .body(ParcelTrackingResponse.class);
    }
}
