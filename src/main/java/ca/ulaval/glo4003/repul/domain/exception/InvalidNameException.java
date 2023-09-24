package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidNameException extends RepULException {
    public InvalidNameException() {
        super("The given name is invalid.");
    }
}
