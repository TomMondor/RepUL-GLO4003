package ca.ulaval.glo4003.repul.cooking.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.cooking.domain.Recipe;

public record RecipePayload(
    String name,
    int calories,
    List<IngredientPayload> ingredients
) {
    public static RecipePayload from(Recipe recipe) {
        return new RecipePayload(
            recipe.name(),
            recipe.calories(),
            recipe.ingredients().stream().map(IngredientPayload::from).toList()
        );
    }
}
