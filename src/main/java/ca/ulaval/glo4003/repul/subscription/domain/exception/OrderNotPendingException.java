package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class OrderNotPendingException extends SubscriptionException {
    public OrderNotPendingException() {
        super("This order is not pending, cannot confirm or deny order.");
    }
}
