package pl.bartoszsredzinski.inpostTracker.exception;

public class ParcelNotFoundException extends RuntimeException{

    public ParcelNotFoundException(String message){
        super("Parcel not found: " + message);
    }
}
