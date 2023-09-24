package ca.ulaval.glo4003.repul.small.domain.account;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.IDUL;
import ca.ulaval.glo4003.repul.domain.exception.InvalidIDULException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IDULTest {
    private static final String AN_EMPTY_IDUL = "";
    private static final String AN_IDUL_WITH_MORE_THAN_FIVE_LETTERS = "ALMATHBOB69";
    private static final String AN_IDUL_NOT_STARTING_WITH_FIVE_LETTERS = "69ALMATH";
    private static final String AN_IDUL_WITH_MORE_THAN_THREE_NUMBERS = "ALMATH69422";
    private static final String A_VALID_IDUL = "ALMAT69";

    @Test
    public void givenEmptyValue_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_EMPTY_IDUL));
    }

    @Test
    public void givenValueWithoutExactlyFiveLetters_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_WITH_MORE_THAN_FIVE_LETTERS));
    }

    @Test
    public void givenValueNotStartingWithFiveLetters_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_NOT_STARTING_WITH_FIVE_LETTERS));
    }

    @Test
    public void givenValueWithMoreThanThreeNumbers_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_WITH_MORE_THAN_THREE_NUMBERS));
    }

    @Test
    public void givenValidValue_whenCreatingIDUL_shouldNotThrowInvalidIDULException() {
        IDUL idul = new IDUL(A_VALID_IDUL);

        assertEquals(A_VALID_IDUL, idul.value());
    }
}
