package ca.ulaval.glo4003.repul.small.domain.account.subscription.order.lunchbox;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.exception.IngredientsMissmatchException;
import ca.ulaval.glo4003.repul.fixture.IngredientFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IngredientTest {
    private static final Ingredient AN_INGREDIENT = new IngredientFixture()
        .withName("Apple").withQuantityInGrams(5.00).build();
    private static final Ingredient ANOTHER_INGREDIENT = new IngredientFixture()
        .withName("Apple").withQuantityInGrams(10.00).build();
    private static final Ingredient AN_INGREDIENT_WITH_ANOTHER_NAME = new IngredientFixture()
        .withName("Orange").withQuantityInGrams(5.00).build();

    @Test
    public void givenIngredientsWithSameName_whenAddingIngredients_shouldAddQuantity() {
        Quantity expectedQuantity = new Quantity(AN_INGREDIENT.quantity().value() +
            ANOTHER_INGREDIENT.quantity().value(), AN_INGREDIENT.quantity().unit());

        Ingredient resultIngredient = AN_INGREDIENT.add(ANOTHER_INGREDIENT);

        assertEquals(expectedQuantity, resultIngredient.quantity());
    }

    @Test
    public void givenDifferentIngredient_whenAddingIngredients_shouldThrowDifferentIngredientsException() {
        assertThrows(IngredientsMissmatchException.class, () ->
            AN_INGREDIENT.add(AN_INGREDIENT_WITH_ANOTHER_NAME));
    }
}
