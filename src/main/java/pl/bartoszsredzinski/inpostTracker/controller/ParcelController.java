package pl.bartoszsredzinski.inpostTracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartoszsredzinski.inpostTracker.service.InpostTrackingClient;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController{
    private final InpostTrackingClient trackingClient;

    public ParcelController(InpostTrackingClient trackingClient){
        this.trackingClient = trackingClient;
    }

    @GetMapping("/{trackingNumber}")
    public JsonNode getTracking(@PathVariable String trackingNumber){
        return trackingClient.getTracking(trackingNumber);
    }
}
