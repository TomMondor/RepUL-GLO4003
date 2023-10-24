package ca.ulaval.glo4003.repul.shipping.domain.exception;

public class InvalidLockerIdException extends ShippingException {
    public InvalidLockerIdException() {
        super("The given locker id is invalid.");
    }
}
