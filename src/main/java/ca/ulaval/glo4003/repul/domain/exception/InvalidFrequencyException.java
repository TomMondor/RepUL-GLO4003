package ca.ulaval.glo4003.repul.domain.exception;

public class InvalidFrequencyException extends RepULException {
    public InvalidFrequencyException() {
        super("The given frequency is invalid.");
    }
}
