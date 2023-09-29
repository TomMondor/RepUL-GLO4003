package ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox;

import ca.ulaval.glo4003.repul.domain.exception.InvalidLunchboxTypeException;

public enum LunchboxType {
    STANDARD;

    public static LunchboxType from(String type) {
        try {
            return LunchboxType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidLunchboxTypeException();
        }
    }
}
