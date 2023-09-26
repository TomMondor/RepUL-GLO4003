package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidIngredientTypeException extends RepULException {
    public InvalidIngredientTypeException() {
        super("Ingredient type must have a name and a price.");
    }
}
