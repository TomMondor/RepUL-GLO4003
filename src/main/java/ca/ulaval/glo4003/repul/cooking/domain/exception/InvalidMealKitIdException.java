package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class InvalidMealKitIdException extends CookingException {
    public InvalidMealKitIdException() {
        super("The given meal kit id is invalid.");
    }
}
