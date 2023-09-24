package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidBirthdateException extends RepULException {
    public InvalidBirthdateException() {
        super("The given birthdate is invalid.");
    }
}
