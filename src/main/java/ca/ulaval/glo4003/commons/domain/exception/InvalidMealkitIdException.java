package ca.ulaval.glo4003.commons.domain.exception;

public class InvalidMealkitIdException extends CommonException {
    public InvalidMealkitIdException() {
        super("The given mealkit id is invalid.");
    }
}
