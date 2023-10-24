package ca.ulaval.glo4003.repul.user.domain.account.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidNameException extends UserException {
    public InvalidNameException() {
        super("The given name is invalid.");
    }
}
