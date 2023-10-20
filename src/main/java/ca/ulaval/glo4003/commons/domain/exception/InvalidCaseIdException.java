package ca.ulaval.glo4003.commons.domain.exception;

public class InvalidCaseIdException extends CommonException {
    public InvalidCaseIdException() {
        super("The given case id is invalid.");
    }
}
