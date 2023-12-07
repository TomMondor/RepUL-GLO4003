package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class SubscriberNotFound extends SubscriptionException {
    public SubscriberNotFound() {
        super("The given subscriber was not found.");
    }
}
