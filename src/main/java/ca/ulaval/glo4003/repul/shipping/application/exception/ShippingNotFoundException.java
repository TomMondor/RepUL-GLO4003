package ca.ulaval.glo4003.repul.shipping.application.exception;

import ca.ulaval.glo4003.repul.shipping.domain.exception.ShippingException;

public class ShippingNotFoundException extends ShippingException {
    public ShippingNotFoundException() {
        super("There is currently no initialized Shipping.");
    }
}
