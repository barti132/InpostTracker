package pl.bartoszsredzinski.inpostTracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import pl.bartoszsredzinski.inpostTracker.dto.ParcelTrackingResponse;
import pl.bartoszsredzinski.inpostTracker.exception.InvalidTrackingNumberException;
import pl.bartoszsredzinski.inpostTracker.exception.ParcelNotFoundException;
import pl.bartoszsredzinski.inpostTracker.service.ParcelService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParcelController.class)
class ParcelControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParcelService parcelService;

    @Test
    void getTrackingShouldReturn200AndParcelDataWhenParcelExists() throws Exception{
        ParcelTrackingResponse.TrackingDetail detail = new ParcelTrackingResponse.TrackingDetail("origin_status", "delivered",
                OffsetDateTime.parse("2026-01-01T10:00:00+01:00"));
        ParcelTrackingResponse response = new ParcelTrackingResponse("123", "inpost_locker_standard", "parcel", "delivered",
                List.of(detail), OffsetDateTime.parse("2026-01-01T10:00:00+01:00"));

        when(parcelService.getTracking("123")).thenReturn(response);

        mockMvc.perform(get("/api/parcels/{trackingNumber}", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tracking_number").value("123"))
                .andExpect(jsonPath("$.status").value("delivered"))
                .andExpect(jsonPath("$.tracking_details.length()").value(1))
                .andExpect(jsonPath("$.tracking_details[0].status").value("delivered"));

        verify(parcelService).getTracking("123");
    }

    @Test
    void getTrackingShouldReturn400WhenServiceThrowsInvalidTrackingNumberException() throws Exception{
        when(parcelService.getTracking("123")).thenThrow(new InvalidTrackingNumberException());

        mockMvc.perform(get("/api/parcels/{trackingNumber}", "123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_TRACKING_NUMBER"))
                .andExpect(jsonPath("$.message").value("Invalid tracking number"));
    }

    @Test
    void getTrackingShouldReturn404WhenServiceThrowsParcelNotFoundException() throws Exception{
        when(parcelService.getTracking("999")).thenThrow(new ParcelNotFoundException("999"));

        mockMvc.perform(get("/api/parcels/{trackingNumber}", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PARCEL_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Parcel not found: 999"));
    }

    @Test
    void getTrackingShouldReturn502WhenServiceThrowsRestClientException() throws Exception{
        when(parcelService.getTracking("123")).thenThrow(new RestClientException("InPost is down"));

        mockMvc.perform(get("/api/parcels/{trackingNumber}", "123"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("INPOST_API_ERROR"))
                .andExpect(jsonPath("$.message").value("Failed to fetch data from InPost"));
    }

    @Test
    void getTrackingShouldReturn500WhenServiceThrowsUnexpectedException() throws Exception{
        when(parcelService.getTracking("123")).thenThrow(new RuntimeException("secret internal details"));

        mockMvc.perform(get("/api/parcels/{trackingNumber}", "123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }


}