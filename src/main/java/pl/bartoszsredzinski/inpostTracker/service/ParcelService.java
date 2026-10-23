package pl.bartoszsredzinski.inpostTracker.service;

import org.springframework.stereotype.Service;
import pl.bartoszsredzinski.inpostTracker.dto.ParcelTrackingResponse;
import pl.bartoszsredzinski.inpostTracker.exception.InvalidTrackingNumberException;

@Service
public class ParcelService{
    private final InpostTrackingClient inpostTrackingClient;

    public ParcelService(InpostTrackingClient inpostTrackingClient){
        this.inpostTrackingClient = inpostTrackingClient;
    }

    public ParcelTrackingResponse getTracking(String trackingNumber){
        if(trackingNumber == null || trackingNumber.isBlank()){
            throw new InvalidTrackingNumberException();
        }

        return inpostTrackingClient.getTracking(trackingNumber);
    }
}
