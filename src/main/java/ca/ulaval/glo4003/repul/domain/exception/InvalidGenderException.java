package ca.ulaval.glo4003.repul.domain.exception;

import java.util.Arrays;

import ca.ulaval.glo4003.repul.domain.account.Gender;

public class InvalidGenderException extends RepULException {
    public InvalidGenderException() {
        super("The gender must be one of the following: " + Arrays.toString(Gender.values()));
    }
}
