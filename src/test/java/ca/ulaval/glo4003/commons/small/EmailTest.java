package ca.ulaval.glo4003.commons.small;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.Email;
import ca.ulaval.glo4003.commons.exception.InvalidEmailException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {
    private static final String AN_EMPTY_EMAIL = "";
    private static final String AN_INVALID_EMAIL = "this.my.email.com";
    private static final String A_VALID_EMAIL = "anEmail123@site.com";

    @Test
    public void givenEmptyValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(AN_EMPTY_EMAIL));
    }

    @Test
    public void givenInvalidValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(AN_INVALID_EMAIL));
    }

    @Test
    public void givenValidValue_whenCreatingEmail_shouldNotThrowInvalidEmailException() {
        Email email = new Email(A_VALID_EMAIL);

        assertEquals(A_VALID_EMAIL, email.value());
    }
}
