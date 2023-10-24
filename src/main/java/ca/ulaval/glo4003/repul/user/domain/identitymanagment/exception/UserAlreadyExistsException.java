package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException() {
        super("An account with this email address already exists.");
    }
}
