package ca.ulaval.glo4003.repul.shipping.domain.exception;

public class InvalidShipperException extends ShippingException {
    public InvalidShipperException() {
        super("Only the shipper can cancel the shipping");
    }
}
