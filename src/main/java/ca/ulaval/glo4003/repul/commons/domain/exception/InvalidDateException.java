package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidDateException extends CommonException {
    public InvalidDateException() {
        super("The given date is in an invalid date format. Use yyyy-MM-dd.");
    }
}
