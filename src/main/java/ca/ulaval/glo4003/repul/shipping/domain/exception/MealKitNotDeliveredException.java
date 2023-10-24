package ca.ulaval.glo4003.repul.shipping.domain.exception;

public class MealKitNotDeliveredException extends ShippingException {
    public MealKitNotDeliveredException() {
        super("This meal kit has not been delivered.");
    }
}
