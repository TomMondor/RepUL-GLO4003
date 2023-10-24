package ca.ulaval.glo4003.repul.shipping.application.exception;

import ca.ulaval.glo4003.repul.shipping.domain.exception.ShippingException;

public class DeliveryAccountNotFoundException extends ShippingException {
    public DeliveryAccountNotFoundException() {
        super("The given delivery account was not found.");
    }
}
