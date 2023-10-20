package ca.ulaval.glo4003.commons.domain.exception;

public class InvalidLocationException extends RepULException {
    public InvalidLocationException() {
        super("The given location is invalid.");
    }
}
