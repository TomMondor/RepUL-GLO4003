package ca.ulaval.glo4003.shipping.domain.exception;

public class InvalidShipperException extends ShippingException {
    public InvalidShipperException() {
        super("Only the shipper can cancel the shipping");
    }
}
