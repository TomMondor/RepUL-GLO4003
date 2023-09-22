package ca.ulaval.glo4003.repul.domain.exception;

public abstract class RepULException extends RuntimeException {
    public RepULException(String message) {
        super(message);
    }
}
