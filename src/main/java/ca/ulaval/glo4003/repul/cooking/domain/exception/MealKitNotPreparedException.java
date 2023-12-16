package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class MealKitNotPreparedException extends CookingException {
    public MealKitNotPreparedException() {
        super("This meal kit has not been prepared yet.");
    }
}
