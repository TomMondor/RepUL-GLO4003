package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class InvalidCookIdException extends CookingException {
    public InvalidCookIdException() {
        super(String.format("The given cook id is invalid."));
    }
}
