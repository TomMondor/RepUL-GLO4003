package ca.ulaval.glo4003.repul.shipping.application.exception;

import ca.ulaval.glo4003.repul.shipping.domain.exception.ShippingException;

public class DeliveryPersonNotFoundException extends ShippingException {
    public DeliveryPersonNotFoundException() {
        super("The given delivery person was not found.");
    }
}
