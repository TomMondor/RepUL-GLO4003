package ca.ulaval.glo4003.repul.domain.exception;

public class UserAlreadyExistsException extends RepULException {
    public UserAlreadyExistsException() {
        super("A user with this email already exists.");
    }
}
