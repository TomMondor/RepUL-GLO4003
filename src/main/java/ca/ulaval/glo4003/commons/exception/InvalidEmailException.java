package ca.ulaval.glo4003.commons.exception;

public class InvalidEmailException extends CommonException {
    public InvalidEmailException() {
        super("The given email is invalid.");
    }
}
