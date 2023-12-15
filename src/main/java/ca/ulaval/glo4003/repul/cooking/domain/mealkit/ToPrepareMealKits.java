package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitCannotBeSelectedException;

public class ToPrepareMealKits extends KitchenMealKits {
    public ToPrepareMealKits() {
        super();
    }

    public List<MealKit> getMealKitsToCook() {
        return mealKits.values().stream().filter(MealKit::isToCookToday).toList();
    }

    public List<MealKit> removeMealKitsToSelect(List<MealKitUniqueIdentifier> mealKitIds) {
        verifyMealKitsCanBeSelected(mealKitIds);

        List<MealKit> mealKitsToSelect = mealKits.values().stream().filter(mealKit -> mealKitIds.contains(mealKit.getMealKitId())).toList();
        mealKitsToSelect.forEach(mealKit -> mealKits.remove(mealKit.getMealKitId()));
        return mealKitsToSelect;
    }

    private void verifyMealKitsCanBeSelected(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(this::verifyMealKitCanBeSelected);
    }

    private void verifyMealKitCanBeSelected(MealKitUniqueIdentifier mealKitId) {
        if (!mealKits.containsKey(mealKitId) || !mealKits.get(mealKitId).isToCookToday()) {
            throw new MealKitCannotBeSelectedException(mealKitId);
        }
    }
}
