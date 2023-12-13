package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class LockerNotFoundException extends DeliveryException {
    public LockerNotFoundException() {
        super("The locker wanted is not found.");
    }
}
