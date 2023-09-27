package ca.ulaval.glo4003.repul.small.domain.account.subscription.order.lunchbox;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.exception.InvalidQuantityException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuantityTest {
    private static final double A_NEGATIVE_VALUE = -1;
    private static final String A_UNIT = "kg";

    @Test
    public void givenNegativeValue_whenCreatingQuantity_shouldThrowInvalidQuantityException() {
        assertThrows(InvalidQuantityException.class, () -> new Quantity(A_NEGATIVE_VALUE, A_UNIT));
    }
}
