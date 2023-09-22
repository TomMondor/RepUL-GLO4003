package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidSubscriptionIdException extends RepULException {
    public InvalidSubscriptionIdException() {
        super("The given subscription id is invalid.");
    }
}
