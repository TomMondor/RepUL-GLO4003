package ca.ulaval.glo4003.identitymanagement.domain.exception;

public class UserAlreadyExistsException extends IAMException {
    public UserAlreadyExistsException() {
        super("A user with this email already exists.");
    }
}
