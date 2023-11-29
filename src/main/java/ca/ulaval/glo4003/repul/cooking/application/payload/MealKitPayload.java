package ca.ulaval.glo4003.repul.cooking.application.payload;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;

public record MealKitPayload(
    UniqueIdentifier mealKitId,
    LocalDate deliveryDate,
    List<Recipe> recipes
) {
}
