package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidOrderIdException extends RepULException {
    public InvalidOrderIdException() {
        super("The given order ID is invalid.");
    }
}
