package ca.ulaval.glo4003.repul.delivery.domain.mealkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;

public class MealKits {
    Map<MealKitUniqueIdentifier, MealKit> mealKits;

    public MealKits() {
        mealKits = new HashMap<>();
    }

    public MealKits(List<MealKit> mealKits) {
        this.mealKits = new HashMap<>();
        mealKits.forEach(this::addMealKit);
    }

    public MealKit findById(MealKitUniqueIdentifier mealKitId) {
        if (!mealKits.containsKey(mealKitId)) {
            throw new InvalidMealKitIdException();
        }
        return mealKits.get(mealKitId);
    }

    public List<MealKit> getAllMealKits() {
        return mealKits.values().stream().toList();
    }

    public void addMealKit(MealKit mealKit) {
        mealKits.put(mealKit.getMealKitId(), mealKit);
    }

    public boolean containsMealKit(MealKitUniqueIdentifier mealKitId) {
        return mealKits.containsKey(mealKitId);
    }

    public MealKit removeMealKit(MealKitUniqueIdentifier mealKitId) {
        if (!mealKits.containsKey(mealKitId)) {
            throw new InvalidMealKitIdException();
        }

        return mealKits.remove(mealKitId);
    }

    public List<MealKit> removeMealKits(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(mealKitId -> {
            if (!mealKits.containsKey(mealKitId)) {
                throw new InvalidMealKitIdException();
            }
        });

        return mealKitIds.stream().map(this.mealKits::remove).collect(Collectors.toCollection(ArrayList::new));
    }

    public void markMealKitsAsPickedUp() {
        mealKits.values().forEach(MealKit::markAsPickedUp);
    }

    public boolean isEmpty() {
        return mealKits.isEmpty();
    }

    public void confirmDelivery(MealKitUniqueIdentifier mealKitId) {
        containsMealKit(mealKitId);

        findById(mealKitId).confirmDelivery();
    }

    public MealKit recallDeliveredMealKit(MealKitUniqueIdentifier mealKitId) {
        containsMealKit(mealKitId);

        findById(mealKitId).recallDelivery();

        return mealKits.get(mealKitId);
    }
}
