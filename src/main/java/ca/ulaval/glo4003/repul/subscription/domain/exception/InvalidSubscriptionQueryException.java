package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class InvalidSubscriptionQueryException extends SubscriptionException {
    public InvalidSubscriptionQueryException() {
        super("The query to create a subscription is invalid.");
    }
}
