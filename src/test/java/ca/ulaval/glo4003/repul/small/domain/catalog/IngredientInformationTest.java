package ca.ulaval.glo4003.repul.small.domain.catalog;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.catalog.Amount;
import ca.ulaval.glo4003.repul.domain.exception.InvalidIngredientTypeException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IngredientInformationTest {
    private static final Amount A_VALID_INGREDIENT_TYPE_PRICE = new Amount(1.0);

    @Test
    public void givenBlankValue_whenCreatingIngredientType_shouldThrowInvalidIngredientTypeException() {
        assertThrows(InvalidIngredientTypeException.class,
            () -> new ca.ulaval.glo4003.repul.domain.catalog.IngredientInformation("", A_VALID_INGREDIENT_TYPE_PRICE));
    }
}
