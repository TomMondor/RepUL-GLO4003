package ca.ulaval.glo4003.shipping.domain.exception;

public class InvalidShippingIdException extends ShippingException {
    public InvalidShippingIdException() {
        super("Invalid shipping id");
    }
}
