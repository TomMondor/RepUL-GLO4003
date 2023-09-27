package ca.ulaval.glo4003.repul.domain.exception;

public class OrderAlreadyStartedException extends RepULException {
    public OrderAlreadyStartedException() {
        super("This order has already been started.");
    }
}
