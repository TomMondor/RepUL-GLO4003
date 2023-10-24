package ca.ulaval.glo4003.repul.user.domain.account.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidIDULException extends UserException {
    public InvalidIDULException() {
        super("The given IDUL is invalid.");
    }
}
