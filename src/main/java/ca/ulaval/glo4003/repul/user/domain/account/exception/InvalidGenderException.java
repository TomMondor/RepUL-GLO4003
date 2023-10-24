package ca.ulaval.glo4003.repul.user.domain.account.exception;

import java.util.Arrays;

import ca.ulaval.glo4003.repul.user.domain.account.Gender;
import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidGenderException extends UserException {
    public InvalidGenderException() {
        super("The gender must be one of the following: " + Arrays.toString(Gender.values()));
    }
}
