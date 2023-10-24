package ca.ulaval.glo4003.repul.small.cooking.domain;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidMealKitTypeException;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;
import ca.ulaval.glo4003.repul.fixture.cooking.RecipeFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecipesCatalogTest {
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final List<Recipe> RECIPES = List.of(new RecipeFixture().build());

    private RecipesCatalog recipesCatalog;

    @Test
    public void givenCatalogWithRecipes_whenGetRecipesWithValidType_shouldReturnMatchingRecipes() {
        this.recipesCatalog = new RecipesCatalog(Map.of(A_MEALKIT_TYPE, RECIPES));

        List<Recipe> recipes = recipesCatalog.getRecipes(A_MEALKIT_TYPE);

        assertEquals(RECIPES, recipes);
    }

    @Test
    public void givenEmptyCatalog_whenGetRecipesWithValidType_shouldThrowInvalidMealKitTypeException() {
        this.recipesCatalog = new RecipesCatalog(Map.of());

        assertThrows(InvalidMealKitTypeException.class, () -> recipesCatalog.getRecipes(MealKitType.STANDARD));
    }
}
