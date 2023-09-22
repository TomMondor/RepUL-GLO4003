package ca.ulaval.glo4003.repul.domain.catalog;

import ca.ulaval.glo4003.repul.domain.exception.InvalidSemesterCodeException;

public record SemesterCode(String value) {
    public SemesterCode {
        if (value == null || value.isBlank()) {
            throw new InvalidSemesterCodeException();
        }
    }
}
