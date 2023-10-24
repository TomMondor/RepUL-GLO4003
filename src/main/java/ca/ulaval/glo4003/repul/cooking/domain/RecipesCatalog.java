package ca.ulaval.glo4003.repul.cooking.domain;

import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidMealKitTypeException;

public class RecipesCatalog {
    private Map<MealKitType, List<Recipe>> recipes;

    public RecipesCatalog(Map<MealKitType, List<Recipe>> recipes) {
        this.recipes = recipes;
    }

    public List<Recipe> getRecipes(MealKitType mealKitType) {
        if (recipes.containsKey(mealKitType)) {
            return recipes.get(mealKitType);
        }
        throw new InvalidMealKitTypeException();
    }
}
