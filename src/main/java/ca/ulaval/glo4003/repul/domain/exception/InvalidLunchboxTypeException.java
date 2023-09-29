package ca.ulaval.glo4003.repul.domain.exception;

import java.util.Arrays;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;

public class InvalidLunchboxTypeException extends RepULException {
    public InvalidLunchboxTypeException() {
        super("The lunchbox type must be one of the following: " + Arrays.toString(LunchboxType.values()));
    }
}
