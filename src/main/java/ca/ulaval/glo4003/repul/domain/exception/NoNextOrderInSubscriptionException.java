package ca.ulaval.glo4003.repul.domain.exception;

public class NoNextOrderInSubscriptionException extends RepULException {
    public NoNextOrderInSubscriptionException() {
        super("There is no next order in this subscription.");
    }
}
