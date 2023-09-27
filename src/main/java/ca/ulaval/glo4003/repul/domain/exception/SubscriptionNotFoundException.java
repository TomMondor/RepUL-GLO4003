package ca.ulaval.glo4003.repul.domain.exception;

public class SubscriptionNotFoundException extends RepULException {
    public SubscriptionNotFoundException() {
        super("The given subscription was not found.");
    }
}
