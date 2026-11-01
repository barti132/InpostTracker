package pl.bartoszsredzinski.inpostTracker.exception;

public class ParcelNotFoundException extends RuntimeException{

    public ParcelNotFoundException(String trackingNumber){
        super("Parcel not found: " + trackingNumber);
    }
}
