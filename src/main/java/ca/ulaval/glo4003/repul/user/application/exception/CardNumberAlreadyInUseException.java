package ca.ulaval.glo4003.repul.user.application.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class CardNumberAlreadyInUseException extends UserException {
    public CardNumberAlreadyInUseException() {
        super("The card number is already in use.");
    }
}
