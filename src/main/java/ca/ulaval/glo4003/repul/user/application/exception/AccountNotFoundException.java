package ca.ulaval.glo4003.repul.user.application.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class AccountNotFoundException extends UserException {
    public AccountNotFoundException() {
        super("The given account was not found.");
    }
}
