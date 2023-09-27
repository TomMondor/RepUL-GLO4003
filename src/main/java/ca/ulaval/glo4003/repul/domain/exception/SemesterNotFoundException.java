package ca.ulaval.glo4003.repul.domain.exception;

public class SemesterNotFoundException extends RepULException {
    public SemesterNotFoundException() {
        super("Could not find the semester matching current date.");
    }
}
