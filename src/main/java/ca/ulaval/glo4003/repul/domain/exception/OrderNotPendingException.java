package ca.ulaval.glo4003.repul.domain.exception;

public class OrderNotPendingException extends RepULException {
    public OrderNotPendingException() {
        super("This order is not pending, cannot confirm or deny order.");
    }
}
