package ca.ulaval.glo4003.shipping.domain.exception;

public class ShippingNotFoundException extends ShippingException {
    public ShippingNotFoundException() {
        super("There is currently no initialized Shipping.");
    }
}
