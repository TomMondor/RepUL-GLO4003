package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class InvalidIDULException extends SubscriptionException {
    public InvalidIDULException() {
        super("The given IDUL is invalid.");
    }
}
