package ca.ulaval.glo4003.repul.commons.domain;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidCardNumberException;

public record SubscriberCardNumber(
    String value
) {
    public SubscriberCardNumber {
        if (value == null || !value.matches("[0-9]{9}")) {
            throw new InvalidCardNumberException();
        }
    }
}
