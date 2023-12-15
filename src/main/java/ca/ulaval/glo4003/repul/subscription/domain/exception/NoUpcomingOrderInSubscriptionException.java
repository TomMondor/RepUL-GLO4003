package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class NoUpcomingOrderInSubscriptionException extends SubscriptionException {
    public NoUpcomingOrderInSubscriptionException() {
        super("There is no upcoming order left in this subscription.");
    }
}
