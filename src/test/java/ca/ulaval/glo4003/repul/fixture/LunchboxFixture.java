package ca.ulaval.glo4003.repul.fixture;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;

public class LunchboxFixture {
    private List<Recipe> recipes;

    public LunchboxFixture() {
        this.recipes = List.of(new RecipeFixture().build(), new RecipeFixture().withName("Spaghetti Carbonara").withCalories(10).withIngredients(
            List.of(new IngredientFixture().withName("cream").build(), new IngredientFixture().withName("tomato").withUnitLessQuantity(2).build(),
                new IngredientFixture().withName("pasta").withQuantityInGrams(100).build())).build());
    }

    public LunchboxFixture withRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        return this;
    }

    public LunchboxFixture addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
        return this;
    }

    public Lunchbox build() {
        return new Lunchbox(recipes);
    }
}
