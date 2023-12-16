package ca.ulaval.glo4003.repul.cooking.application.payload;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.recipe.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.recipe.Recipe;

public record MealKitsPayload(
    List<MealKitPayload> mealKits,
    List<IngredientPayload> totalIngredients
) {
    public static MealKitsPayload from(List<MealKit> mealKitsToPrepare) {
        return new MealKitsPayload(
            mealKitsToPrepare.stream().map(MealKitPayload::from).toList(),
            combineIngredients(mealKitsToPrepare)
        );
    }

    private static List<IngredientPayload> combineIngredients(List<MealKit> mealKits) {
        List<Ingredient> combinedIngredients = new ArrayList<>();
        List<Ingredient> allIngredients = getAllIngredients(mealKits);

        for (Ingredient ingredient : allIngredients) {
            boolean foundInList = false;
            for (Ingredient ingredientInCombinedIngredients : combinedIngredients) {
                if (ingredient.ingredient().equals(ingredientInCombinedIngredients.ingredient())) {
                    foundInList = true;
                    Ingredient ingredientWithNewQuantity = ingredientInCombinedIngredients.add(ingredient.quantity());
                    combinedIngredients.remove(ingredientInCombinedIngredients);
                    combinedIngredients.add(ingredientWithNewQuantity);
                    break;
                }
            }
            if (!foundInList) {
                combinedIngredients.add(ingredient);
            }
        }

        return combinedIngredients.stream().map(IngredientPayload::from).toList();
    }

    private static List<Ingredient> getAllIngredients(List<MealKit> mealKits) {
        List<Ingredient> allIngredients = new ArrayList<>();
        for (MealKit mealkit : mealKits) {
            for (Recipe recipe : mealkit.getRecipes()) {
                allIngredients.addAll(recipe.ingredients());
            }
        }
        return allIngredients;
    }
}
