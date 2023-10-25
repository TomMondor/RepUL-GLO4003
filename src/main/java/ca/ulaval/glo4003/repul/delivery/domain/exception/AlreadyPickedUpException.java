package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class AlreadyPickedUpException extends DeliveryException {
    public AlreadyPickedUpException() {
        super("The meal kit is already picked up.");
    }
}
