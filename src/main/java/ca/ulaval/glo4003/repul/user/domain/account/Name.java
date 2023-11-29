package ca.ulaval.glo4003.repul.user.domain.account;

import ca.ulaval.glo4003.repul.user.domain.account.exception.InvalidNameException;

public record Name(
    String value
) {
    public Name {
        if (value.isBlank()) {
            throw new InvalidNameException();
        }
    }
}
