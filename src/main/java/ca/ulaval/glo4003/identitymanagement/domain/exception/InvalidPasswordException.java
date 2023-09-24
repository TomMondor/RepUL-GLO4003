package ca.ulaval.glo4003.identitymanagement.domain.exception;

public class InvalidPasswordException extends IAMException {
    public InvalidPasswordException() {
        super("The given password is invalid.");
    }
}
