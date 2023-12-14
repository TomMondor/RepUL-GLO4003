package ca.ulaval.glo4003.repul.cooking.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public record SelectionPayload(
    List<String> mealKitSelectionIds
) {
    public static SelectionPayload from(List<MealKitUniqueIdentifier> mealKitSelectionIds) {
        return new SelectionPayload(mealKitSelectionIds.stream().map(mealKitSelectionId -> mealKitSelectionId.getUUID().toString()).toList());
    }
}
