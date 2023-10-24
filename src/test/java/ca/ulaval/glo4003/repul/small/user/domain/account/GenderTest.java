package ca.ulaval.glo4003.repul.small.user.domain.account;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.user.domain.account.Gender;
import ca.ulaval.glo4003.repul.user.domain.account.exception.InvalidGenderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenderTest {
    private static final String AN_INVALID_GENDER = "combat helicopter";
    private static final String A_VALID_GENDER = Gender.WOMAN.toString();

    @Test
    public void givenAnInvalidGender_whenFrom_thenThrowInvalidGenderException() {
        assertThrows(InvalidGenderException.class, () -> Gender.from(AN_INVALID_GENDER));
    }

    @Test
    public void givenAValidGender_whenFrom_thenReturnGenderEnum() {
        assertEquals(Gender.WOMAN, Gender.from(A_VALID_GENDER));
    }
}
