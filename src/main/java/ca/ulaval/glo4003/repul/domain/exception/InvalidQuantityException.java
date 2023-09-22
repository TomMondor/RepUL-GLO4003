package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidQuantityException extends RepULException {
    public InvalidQuantityException() {
        super("The given quantity is invalid.");
    }
}
