package pl.bartoszsredzinski.inpostTracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import pl.bartoszsredzinski.inpostTracker.dto.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(InvalidTrackingNumberException.class)
    public ResponseEntity<ApiError> handleInvalidTrackingNumberException(InvalidTrackingNumberException e){
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_TRACKING_NUMBER", "Numer przesyłki nie może być pusty"));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> handleRestClientException(RestClientException e){
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiError("INPOST_API_ERROR", "Nie udało się pobrać danych z Inpost"));
    }
}
