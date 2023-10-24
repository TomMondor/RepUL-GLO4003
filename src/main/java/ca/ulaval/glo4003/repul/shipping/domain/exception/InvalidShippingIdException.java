package ca.ulaval.glo4003.repul.shipping.domain.exception;

public class InvalidShippingIdException extends ShippingException {
    public InvalidShippingIdException() {
        super("Invalid shipping id");
    }
}
