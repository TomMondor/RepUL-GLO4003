package ca.ulaval.glo4003.repul.small.domain.account.subscription.order.lunchbox;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.exception.InvalidQuantityException;

import static org.junit.jupiter.api.Assertions.*;

public class QuantityTest {
    private static final int INVALID_VALUE = -1;
    private static final int VALID_VALUE = 1;
    private static final int ANOTHER_VALID_VALUE = 3;
    private static final String VALID_UNIT = "kg";
    private static final String OTHER_UNIT = "lb";
    private static final Quantity A_QUANTITY = new Quantity(VALID_VALUE, VALID_UNIT);
    @Test
    public void givenValueBelowZero_whenCreatingQuantity_shouldThrowInvalidQuantityException() {
        assertThrows(InvalidQuantityException.class, () -> new Quantity(INVALID_VALUE, VALID_UNIT));
    }

    @Test
    public void givenValidParams_whenCreatingQuantity_shouldNotThrow() {
        assertDoesNotThrow(() -> new Quantity(VALID_VALUE, VALID_UNIT));
    }

    @Test
    public void whenAddingQuantity_shouldAddValues() {
        Quantity quantityToAdd = new Quantity(ANOTHER_VALID_VALUE, VALID_UNIT);

        Quantity addedQuantity = A_QUANTITY.add(quantityToAdd);

        assertEquals(A_QUANTITY.value() + quantityToAdd.value(), addedQuantity.value());
    }

    @Test
    public void givenQuantityWithDifferentUnit_whenAddingQuantity_shouldThrowInvalidQuantityException() {
        Quantity quantityToAdd = new Quantity(ANOTHER_VALID_VALUE, OTHER_UNIT);

        assertThrows(InvalidQuantityException.class,
            () -> A_QUANTITY.add(quantityToAdd));
    }
}
