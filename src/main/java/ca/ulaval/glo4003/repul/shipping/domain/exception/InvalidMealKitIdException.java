package ca.ulaval.glo4003.repul.shipping.domain.exception;

public class InvalidMealKitIdException extends ShippingException {
    public InvalidMealKitIdException() {
        super("This meal kit id is invalid.");
    }
}
