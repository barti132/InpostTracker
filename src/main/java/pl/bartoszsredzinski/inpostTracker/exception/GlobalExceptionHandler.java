package pl.bartoszsredzinski.inpostTracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import pl.bartoszsredzinski.inpostTracker.dto.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler{

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidTrackingNumberException.class)
    public ResponseEntity<ApiError> handleInvalidTrackingNumberException(InvalidTrackingNumberException e){
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_TRACKING_NUMBER", e.getMessage()));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> handleRestClientException(RestClientException e){
        log.warn("Inpost API error", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiError("INPOST_API_ERROR", e.getMessage()));
    }

    @ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<ApiError> handleParcelNotFoundException(ParcelNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("PARCEL_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", e.getMessage()));
    }
}
