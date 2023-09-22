package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidSemesterCodeException extends RepULException {
    public InvalidSemesterCodeException() {
        super("The given semester code is invalid.");
    }
}
