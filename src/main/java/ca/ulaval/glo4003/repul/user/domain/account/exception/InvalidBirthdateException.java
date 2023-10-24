package ca.ulaval.glo4003.repul.user.domain.account.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidBirthdateException extends UserException {
    public InvalidBirthdateException() {
        super("The given birthdate is invalid.");
    }
}
