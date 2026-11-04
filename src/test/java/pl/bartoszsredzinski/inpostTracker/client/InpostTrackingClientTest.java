package pl.bartoszsredzinski.inpostTracker.client;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import pl.bartoszsredzinski.inpostTracker.dto.ParcelTrackingResponse;
import pl.bartoszsredzinski.inpostTracker.exception.ParcelNotFoundException;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class InpostTrackingClientTest{
    private static final String BASE_URL = "http://localhost";
    private static final String TRACKING_URL = BASE_URL + "/v1/tracking/123";

    private MockRestServiceServer server;
    private InpostTrackingClient client;

    @BeforeEach
    void setUp(){
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder)
                .build();
        client = new InpostTrackingClient(builder, BASE_URL);
    }

    @Test
    void getTrackingShouldMapResponseWhenInpostReturns200(){
        String json = """
                {
                  "tracking_number": "123",
                  "service": "inpost_locker_standard",
                  "type": "parcel",
                  "status": "delivered",
                  "updated_at": "2026-01-01T10:00:00.000+01:00",
                  "unknown_field": "should be ignored",
                  "tracking_details": [
                    {
                      "origin_status": "delivered_origin",
                      "status": "delivered",
                      "datetime": "2026-01-01T09:00:00.000+01:00"
                    }
                  ]
                }
                """;

        server.expect(requestTo(TRACKING_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ParcelTrackingResponse result = client.getTracking("123");

        assertEquals("123", result.trackingNumber());
        assertEquals("inpost_locker_standard", result.service());
        assertEquals("parcel", result.type());
        assertEquals("delivered", result.status());
        assertEquals(1, result.trackingDetails()
                .size());
        assertEquals("delivered_origin", result.trackingDetails()
                .get(0)
                .originStatus());
        assertEquals("delivered", result.trackingDetails()
                .get(0)
                .status());

        OffsetDateTime expected = OffsetDateTime.parse("2026-01-01T10:00:00+01:00");
        assertTrue(expected.isEqual(result.updatedAt()));

        server.verify();
    }

    @Test
    void getTrackingShouldThrowParcelNotFoundExceptionWhenInpostReturns404(){
        server.expect(requestTo(TRACKING_URL))
                .andRespond(withResourceNotFound());

        ParcelNotFoundException e = assertThrows(ParcelNotFoundException.class, () -> client.getTracking("123"));

        assertEquals("Parcel not found: 123", e.getMessage());
        server.verify();
    }

    @Test
    void getTrackingShouldThrowRestClientExceptionWhenInpostReturns500(){
        server.expect(requestTo(TRACKING_URL))
                .andRespond(withServerError());

        assertThrows(HttpServerErrorException.InternalServerError.class, () -> client.getTracking("123"));

        server.verify();
    }

    @Test
    void getTrackingShouldReturnEmptyHistoryWhenTrackingDetailsIsEmptyList(){
        String json = """
                {
                  "tracking_number": "123",
                  "status": "confirmed",
                  "tracking_details": []
                }
                """;

        server.expect(requestTo(TRACKING_URL))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ParcelTrackingResponse result = client.getTracking("123");

        assertEquals("123", result.trackingNumber());
        assertNotNull(result.trackingDetails());
        assertTrue(result.trackingDetails()
                .isEmpty());
        server.verify();
    }

    @Test
    void getTrackingShouldReturnEmptyHistoryWhenTrackingDetailsFieldIsMissing(){
        String json = """
                {
                  "tracking_number": "123",
                  "status": "confirmed"
                }
                """;

        server.expect(requestTo(TRACKING_URL))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ParcelTrackingResponse result = client.getTracking("123");

        assertEquals("123", result.trackingNumber());
        assertNotNull(result.trackingDetails());
        assertTrue(result.trackingDetails()
                .isEmpty());
        server.verify();
    }
}