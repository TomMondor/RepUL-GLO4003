package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;

public abstract class KitchenMealKits {
    Map<MealKitUniqueIdentifier, MealKit> mealKits;

    protected KitchenMealKits() {
        mealKits = new HashMap<>();
    }

    public void add(MealKit mealKit) {
        mealKits.put(mealKit.getMealKitId(), mealKit);
    }

    public void add(List<MealKit> mealKits) {
        mealKits.forEach(this::add);
    }

    protected MealKit getMealKit(MealKitUniqueIdentifier mealKitId) {
        if (!mealKits.containsKey(mealKitId)) {
            throw new MealKitNotFoundException(mealKitId);
        }
        return mealKits.get(mealKitId);
    }
}
