package pl.bartoszsredzinski.inpostTracker.exception;

public class InvalidTrackingNumberException extends RuntimeException{

    public InvalidTrackingNumberException(){
        super("Invalid tracking number");
    }
}
