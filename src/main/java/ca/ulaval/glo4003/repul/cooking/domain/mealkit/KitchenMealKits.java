package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitCannotBeSelectedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotForKitchenPickUpException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;

public class KitchenMealKits {
    Map<MealKitUniqueIdentifier, MealKit> kitchenMealKits;

    public KitchenMealKits() {
        kitchenMealKits = new HashMap<>();
    }

    public void add(MealKit mealKit) {
        kitchenMealKits.put(mealKit.getMealKitId(), mealKit);
    }

    public void add(List<MealKit> mealKits) {
        mealKits.forEach(this::add);
    }

    public List<MealKit> getMealKitsToCook() {
        return kitchenMealKits.values().stream().filter(MealKit::isToCookToday).toList();
    }

    public List<MealKit> removeMealKitsToSelect(List<MealKitUniqueIdentifier> mealKitIds) {
        verifyMealKitsCanBeSelected(mealKitIds);

        List<MealKit> mealKitsToSelect = kitchenMealKits.values().stream().filter(mealKit -> mealKitIds.contains(mealKit.getMealKitId())).toList();
        mealKitsToSelect.forEach(mealKit -> kitchenMealKits.remove(mealKit.getMealKitId()));
        return mealKitsToSelect;
    }

    public MealKit pickUpNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        if (!kitchenMealKits.containsKey(mealKitId)) {
            throw new MealKitNotFoundException(mealKitId);
        }
        MealKit mealKit = kitchenMealKits.get(mealKitId);

        if (!mealKit.isForSubscriber(subscriberId)) {
            throw new MealKitNotFoundException(mealKitId);
        } else if (!mealKit.isCooked()) {
            throw new MealKitNotCookedException();
        } else if (mealKit.isToBeDelivered()) {
            throw new MealKitNotForKitchenPickUpException(mealKitId);
        }

        kitchenMealKits.remove(mealKitId);

        return mealKit;
    }

    public void removeMealKits(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(id -> kitchenMealKits.remove(id));
    }

    public MealKit getMealKit(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = kitchenMealKits.get(mealKitId);
        if (mealKit == null) {
            throw new MealKitNotFoundException(mealKitId);
        }
        return mealKit;
    }

    public MealKit recall(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = getMealKit(mealKitId);
        kitchenMealKits.remove(mealKitId);

        mealKit.recall();
        return mealKit;
    }

    private void verifyMealKitsCanBeSelected(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(this::verifyMealKitCanBeSelected);
    }

    private void verifyMealKitCanBeSelected(MealKitUniqueIdentifier mealKitId) {
        if (!kitchenMealKits.containsKey(mealKitId) || !kitchenMealKits.get(mealKitId).isToCookToday()) {
            throw new MealKitCannotBeSelectedException(mealKitId);
        }
    }
}
