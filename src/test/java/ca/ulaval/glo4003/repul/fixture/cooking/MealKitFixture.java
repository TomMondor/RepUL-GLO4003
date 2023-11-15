package ca.ulaval.glo4003.repul.fixture.cooking;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.domain.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;

public class MealKitFixture {
    private UniqueIdentifier mealKitId;
    private List<Recipe> recipes;
    private LocalDate deliveryDate;

    public MealKitFixture() {
        this.mealKitId = new UniqueIdentifierFactory().generate();
        this.recipes = List.of(new RecipeFixture().build());
        this.deliveryDate = LocalDate.of(2020, 1, 1);
    }

    public MealKitFixture withMealKitId(UniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
        return this;
    }

    public MealKitFixture withRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        return this;
    }

    public MealKitFixture withDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public MealKit build() {
        return new MealKit(mealKitId, deliveryDate, recipes);
    }
}
