package pl.bartoszsredzinski.inpostTracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartoszsredzinski.inpostTracker.dto.ParcelTrackingResponse;
import pl.bartoszsredzinski.inpostTracker.service.ParcelService;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController{
    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService){
        this.parcelService = parcelService;
    }

    @GetMapping("/{trackingNumber}")
    public ParcelTrackingResponse getTracking(@PathVariable String trackingNumber){
        return parcelService.getTracking(trackingNumber);
    }
}
