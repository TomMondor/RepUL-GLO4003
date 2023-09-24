package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidIDULException extends RepULException {
    public InvalidIDULException() {
        super("The given IDUL is invalid.");
    }
}
