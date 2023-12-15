package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class OrderNotFoundException extends SubscriptionException {
    public OrderNotFoundException() {
        super("The given order was not found.");
    }
}
