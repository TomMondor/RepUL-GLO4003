package ca.ulaval.glo4003.repul.subscription.domain.subscription;

import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidSemesterCodeException;

public record SemesterCode(
    String value
) {
    public SemesterCode {
        if (value == null || value.isBlank()) {
            throw new InvalidSemesterCodeException();
        }
    }
}
