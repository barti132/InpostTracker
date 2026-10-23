package pl.bartoszsredzinski.inpostTracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParcelTrackingResponse(@JsonProperty("tracking_number") String trackingNumber,
                                     String service,
                                     String type,
                                     String status,
                                     @JsonProperty("tracking_details") List<TrackingDetail> trackingDetails,
                                     @JsonProperty("updated_at") OffsetDateTime updatedAt){

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TrackingDetail(@JsonProperty("origin_status") String originStatus, String status, OffsetDateTime datetime){
    }
}