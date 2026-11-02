package pl.bartoszsredzinski.inpostTracker.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.bartoszsredzinski.inpostTracker.dto.ParcelTrackingResponse;
import pl.bartoszsredzinski.inpostTracker.exception.ParcelNotFoundException;

@Component
public class InpostTrackingClient{

    private final RestClient restClient;

    public InpostTrackingClient(RestClient.Builder builder, @Value("${inpost.api.base-url}") String baseUrl){
        restClient = builder.baseUrl(baseUrl)
                .build();
    }

    public ParcelTrackingResponse getTracking(String trackingNumber){
        return restClient.get()
                .uri("/v1/tracking/{trackingNumber}", trackingNumber)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new ParcelNotFoundException(trackingNumber);
                })
                .body(ParcelTrackingResponse.class);
    }
}
