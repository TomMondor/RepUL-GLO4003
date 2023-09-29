package ca.ulaval.glo4003.repul.small.domain.account.subscription.order.lunchbox;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLunchboxTypeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LunchboxTypeTest {
    private static final String AN_INVALID_LUNCHBOX_TYPE = "invalid type";
    private static final String A_VALID_LUNCHBOX_TYPE = LunchboxType.STANDARD.toString();

    @Test
    public void givenInvalidLunchboxType_whenFrom_thenThrowInvalidLunchboxTypeException() {
        assertThrows(InvalidLunchboxTypeException.class, () -> LunchboxType.from(AN_INVALID_LUNCHBOX_TYPE));
    }

    @Test
    public void givenValidLunchboxType_whenFrom_thenReturnLunchboxTypeEnum() {
        assertEquals(LunchboxType.STANDARD, LunchboxType.from(A_VALID_LUNCHBOX_TYPE));
    }
}
