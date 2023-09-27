package ca.ulaval.glo4003.repul.domain.exception;

public class OrderAlreadyDeclinedException extends RepULException {
    public OrderAlreadyDeclinedException() {
        super("This order has already been declined.");
    }
}
