package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidLocationException extends RepULException {
    public InvalidLocationException() {
        super("The given location is invalid.");
    }
}
