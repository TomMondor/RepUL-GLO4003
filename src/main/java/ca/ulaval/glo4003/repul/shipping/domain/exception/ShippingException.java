package ca.ulaval.glo4003.repul.shipping.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

public abstract class ShippingException extends RepULException {
    public ShippingException(String message) {
        super(message);
    }
}
