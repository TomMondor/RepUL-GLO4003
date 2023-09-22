package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidRecipeException extends RepULException {
    public InvalidRecipeException() {
        super("The given recipe is invalid.");
    }
}
