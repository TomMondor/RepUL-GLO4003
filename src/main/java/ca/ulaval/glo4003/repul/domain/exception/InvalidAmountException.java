package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidAmountException extends RepULException {
    public InvalidAmountException() {
        super("Amount must be positive.");
    }
}
