package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidLocationIdException extends RepULException {
    public InvalidLocationIdException() {
        super("The given location Id is invalid.");
    }
}
