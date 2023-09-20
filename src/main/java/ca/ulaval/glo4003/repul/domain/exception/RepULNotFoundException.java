package ca.ulaval.glo4003.repul.domain.exception;

public class RepULNotFoundException extends RepULException {
    public RepULNotFoundException() {
        super("There is currently no initialized RepUL.");
    }
}
