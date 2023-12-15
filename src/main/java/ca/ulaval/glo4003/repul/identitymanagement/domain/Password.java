package ca.ulaval.glo4003.repul.identitymanagement.domain;

import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.InvalidPasswordException;

public record Password(
    String value
) {
    public Password {
        if (value.isBlank()) {
            throw new InvalidPasswordException();
        }
    }
}
