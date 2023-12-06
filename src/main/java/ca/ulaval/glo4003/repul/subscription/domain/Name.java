package ca.ulaval.glo4003.repul.subscription.domain;

import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidNameException;

public record Name(
    String value
) {
    public Name {
        if (value.isBlank()) {
            throw new InvalidNameException();
        }
    }
}
