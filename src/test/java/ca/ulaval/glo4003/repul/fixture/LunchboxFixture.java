package ca.ulaval.glo4003.repul.fixture;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;

public class LunchboxFixture {
    private static final String DEFAULT_RECIPE_NAME = "DEFAULT_RECIPE";
    private static final int DEFAULT_CALORIES = 10;
    private static final Ingredient DEFAULT_INGREDIENT = new Ingredient("butter", new Quantity(1.00, "kg"));
    private List<Recipe> recipe;

    public LunchboxFixture() {
        this.recipe = new ArrayList<>();
        Recipe defaultRecipe = new Recipe(DEFAULT_RECIPE_NAME, DEFAULT_CALORIES, List.of(DEFAULT_INGREDIENT));
        this.recipe.add(defaultRecipe);
    }

    public LunchboxFixture withRecipe(List<Recipe> recipe) {
        this.recipe = recipe;
        return this;
    }

    public LunchboxFixture withNewRecipe(Recipe recipe) {
        this.recipe.add(recipe);
        return this;
    }

    public Lunchbox build() {
        return new Lunchbox(recipe);
    }
}
