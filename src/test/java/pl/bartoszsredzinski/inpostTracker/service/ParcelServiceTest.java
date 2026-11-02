package pl.bartoszsredzinski.inpostTracker.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartoszsredzinski.inpostTracker.client.InpostTrackingClient;
import pl.bartoszsredzinski.inpostTracker.dto.ParcelTrackingResponse;
import pl.bartoszsredzinski.inpostTracker.exception.InvalidTrackingNumberException;
import pl.bartoszsredzinski.inpostTracker.exception.ParcelNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest{
    @Mock
    InpostTrackingClient client;

    @InjectMocks
    ParcelService service;

    @Test
    void getTrackingShouldReturnValidTrackingNumber(){
        ParcelTrackingResponse.TrackingDetail trackingDetail = new ParcelTrackingResponse.TrackingDetail("status", "status",
                OffsetDateTime.parse("2026-01-01T10:00:00+01:00"));
        ParcelTrackingResponse response = new ParcelTrackingResponse("123", "service", "type", "status", List.of(trackingDetail),
                OffsetDateTime.parse("2026-01-01T10:00:00+01:00"));

        when(client.getTracking("123")).thenReturn(response);

        ParcelTrackingResponse result = service.getTracking("123");

        assertEquals(result, response);
        verify(client).getTracking("123");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void getTrackingShouldThrowInvalidTrackingNumberExceptionWhenTrackingNumberIsBlank(String trackingNumber){
        assertThrows(InvalidTrackingNumberException.class, () -> service.getTracking(trackingNumber));

        verifyNoInteractions(client);
    }

    @Test
    void getTrackingShouldPropageteParcelNotFoundExceptionWhenClientThrows(){
        when(client.getTracking("123")).thenThrow(new ParcelNotFoundException("123"));

        assertThrows(ParcelNotFoundException.class, () -> service.getTracking("123"));

        verify(client).getTracking("123");
    }

}