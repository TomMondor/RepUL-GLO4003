package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;

public class MealKits {
    Map<MealKitUniqueIdentifier, MealKit> mealKits;

    public MealKits() {
        mealKits = new HashMap<>();
    }

    public void add(MealKit mealKit) {
        mealKits.put(mealKit.getMealKitId(), mealKit);
    }

    public List<MealKit> getMealKitsToCook() {
        return mealKits.values().stream().filter(MealKit::isToCookToday).toList();
    }

    public void remove(MealKitUniqueIdentifier mealKitId) {
        mealKits.remove(mealKitId);
    }

    public void removeMealKits(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(id -> mealKits.remove(id));
    }

    public MealKit getMealKit(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = mealKits.get(mealKitId);
        if (mealKit == null) {
            throw new MealKitNotFoundException(mealKitId);
        }
        return mealKit;
    }

    public List<MealKitUniqueIdentifier> getMealKitsIdSelectedByCook(CookUniqueIdentifier cookId) {
        return mealKits.values().stream().filter(mealKit -> mealKit.isSelectedBy(cookId)).map(MealKit::getMealKitId).toList();
    }

    public void selectMealKitsByCook(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        selectedMealKitIds.forEach(id -> {
            MealKit mealKit = getMealKit(id);
            mealKit.selectBy(cookId);
        });
    }

    public void unselectMealKits(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(id -> {
            MealKit mealKit = getMealKit(id);
            mealKit.unselect();
        });
    }

    public void unselectMealKitsByCook(CookUniqueIdentifier cookId) {
        mealKits.values().stream().filter(mealKit -> mealKit.isSelectedBy(cookId)).forEach(MealKit::unselect);
    }

    public MealKit confirmMealKitCooked(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = getMealKit(mealKitId);
        mealKit.setCooked();
        return mealKit;
    }

    public List<MealKit> confirmMealKitsCooked(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(id -> {
            MealKit mealKit = getMealKit(id);
            mealKit.setCooked();
        });
        return mealKits.values().stream().filter(mealKit -> mealKitIds.contains(mealKit.getMealKitId())).toList();
    }

    public void recallMealKit(MealKitUniqueIdentifier mealKitId, CookUniqueIdentifier cookId) {
        MealKit mealKit = getMealKit(mealKitId);
        mealKit.recall(cookId);
    }
}
