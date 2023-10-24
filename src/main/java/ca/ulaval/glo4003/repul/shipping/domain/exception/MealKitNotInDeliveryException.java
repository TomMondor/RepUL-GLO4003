package ca.ulaval.glo4003.repul.shipping.domain.exception;

public class MealKitNotInDeliveryException extends ShippingException {
    public MealKitNotInDeliveryException() {
        super("This meal kit is not in delivery.");
    }
}
