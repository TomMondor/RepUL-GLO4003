package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidLunchboxException extends RepULException {
    public InvalidLunchboxException() {
        super("The given lunchbox is invalid.");
    }
}
