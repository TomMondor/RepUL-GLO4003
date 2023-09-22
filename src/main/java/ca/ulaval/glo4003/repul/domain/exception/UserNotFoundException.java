package ca.ulaval.glo4003.repul.domain.exception;

public class UserNotFoundException extends RepULException {
    public UserNotFoundException() {
        super("The given email and password pair is not found.");
    }
}
