package ca.ulaval.glo4003.repul.cooking.application.payload;

import ca.ulaval.glo4003.repul.cooking.domain.recipe.Ingredient;

public record IngredientPayload(
    String name,
    QuantityPayload quantity
) {
    public static IngredientPayload from(Ingredient ingredient) {
        return new IngredientPayload(
            ingredient.ingredient(),
            QuantityPayload.from(ingredient.quantity())
        );
    }
}
