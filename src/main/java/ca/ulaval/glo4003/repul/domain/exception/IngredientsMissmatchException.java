package ca.ulaval.glo4003.repul.domain.exception;

public class IngredientsMissmatchException extends RepULException {
    public IngredientsMissmatchException() {
        super("The two ingredients to add are different.");
    }
}
