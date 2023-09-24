package ca.ulaval.glo4003.repul.domain.account;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.domain.exception.InvalidBirthdateException;

public record Birthdate(LocalDate value) {
    public Birthdate {
        if (value.isAfter(LocalDate.now())) {
            throw new InvalidBirthdateException();
        }
    }
}
