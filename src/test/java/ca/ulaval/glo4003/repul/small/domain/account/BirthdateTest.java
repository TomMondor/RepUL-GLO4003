package ca.ulaval.glo4003.repul.small.domain.account;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.domain.exception.InvalidBirthdateException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BirthdateTest {
    private static final LocalDate A_FUTURE_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate A_VALID_DATE = LocalDate.now().minusYears(18);

    @Test
    public void givenFutureBirthdate_whenCreatingBirthdate_shouldThrowInvalidBirthdateException() {
        assertThrows(InvalidBirthdateException.class, () -> new Birthdate(A_FUTURE_DATE));
    }

    @Test
    public void givenValidValue_whenCreatingBirthdate_shouldNotThrow() {
        assertDoesNotThrow(() -> new Birthdate(A_VALID_DATE));
    }
}
