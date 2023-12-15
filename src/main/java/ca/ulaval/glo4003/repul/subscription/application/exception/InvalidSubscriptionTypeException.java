package ca.ulaval.glo4003.repul.subscription.application.exception;

import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriptionException;

public class InvalidSubscriptionTypeException extends SubscriptionException {
    public InvalidSubscriptionTypeException() {
        super("The given subscription type is invalid.");
    }
}
