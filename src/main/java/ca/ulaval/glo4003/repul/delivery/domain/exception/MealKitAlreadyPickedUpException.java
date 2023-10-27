package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class MealKitAlreadyPickedUpException extends DeliveryException {
    public MealKitAlreadyPickedUpException() {
        super("The meal kit is already picked up.");
    }
}
