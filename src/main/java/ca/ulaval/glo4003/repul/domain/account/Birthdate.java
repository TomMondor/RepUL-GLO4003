package ca.ulaval.glo4003.repul.domain.account;

import java.time.LocalDate;
import java.time.Period;

import ca.ulaval.glo4003.repul.domain.exception.InvalidBirthdateException;

public record Birthdate(LocalDate value) {
    public Birthdate {
        if (value.isAfter(LocalDate.now())) {
            throw new InvalidBirthdateException();
        }
    }

    public Integer getAge() {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(value, currentDate);
        return period.getYears();
    }
}
