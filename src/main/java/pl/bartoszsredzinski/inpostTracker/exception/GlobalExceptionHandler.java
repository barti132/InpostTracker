package pl.bartoszsredzinski.inpostTracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
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
                .body(new ApiError("INPOST_API_ERROR", "Failed to fetch data from InPost"));
    }

    @ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<ApiError> handleParcelNotFoundException(ParcelNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("PARCEL_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFoundException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NO_RESOURCE_FOUND", "Resource not found"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(e.getHeaders())
                .body(new ApiError("METHOD_NOT_ALLOWED", "Method not allowed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e){
        log.error("Unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "Unexpected error"));
    }
}
