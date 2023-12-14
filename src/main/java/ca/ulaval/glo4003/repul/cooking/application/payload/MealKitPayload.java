package ca.ulaval.glo4003.repul.cooking.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;

public record MealKitPayload(
    String mealKitId,
    String deliveryDate,
    List<RecipePayload> recipes
) {
    public static MealKitPayload from(MealKit mealKit) {
        return new MealKitPayload(
            mealKit.getMealKitId().getUUID().toString(),
            mealKit.getDateOfReceipt().toString(),
            mealKit.getRecipes().stream().map(RecipePayload::from).toList()
        );
    }
}
