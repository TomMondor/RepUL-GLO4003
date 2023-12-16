package ca.ulaval.glo4003.repul.cooking.domain.Cook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotInSelectionException;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;

public class CookSelectedMealKits {
    private final Map<MealKitUniqueIdentifier, MealKit> selectedMealKits;

    public CookSelectedMealKits() {
        this.selectedMealKits = new HashMap<>();
    }

    public void selectMealKits(List<MealKit> newSelectedMealKits) {
        newSelectedMealKits.forEach(newSelectedMealKit -> selectedMealKits.put(newSelectedMealKit.getMealKitId(), newSelectedMealKit));
    }

    public List<MealKit> unselectMealKits(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(this::verifyMealKitIsSelected);

        List<MealKit> unselectedMealKits = new ArrayList<>();
        mealKitIds.forEach(mealKitId -> {
            unselectedMealKits.add(selectedMealKits.get(mealKitId));
            selectedMealKits.remove(mealKitId);
        });
        return unselectedMealKits;
    }

    public MealKit confirmMealKitPrepared(MealKitUniqueIdentifier mealKitId) {
        verifyMealKitIsSelected(mealKitId);
        MealKit mealKit = selectedMealKits.get(mealKitId);
        mealKit.confirmPreparation();

        selectedMealKits.remove(mealKitId);
        return mealKit;
    }

    public List<MealKit> confirmMealKitsPrepared(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(this::verifyMealKitIsSelected);
        List<MealKit> mealKits = new ArrayList<>();
        mealKitIds.forEach(mealKitId -> {
            MealKit mealKit = selectedMealKits.get(mealKitId);
            mealKit.confirmPreparation();
            mealKits.add(mealKit);

            selectedMealKits.remove(mealKitId);
        });
        return mealKits;
    }

    public List<MealKitUniqueIdentifier> getIds() {
        return selectedMealKits.values().stream().map(MealKit::getMealKitId).toList();
    }

    private void verifyMealKitIsSelected(MealKitUniqueIdentifier mealKitId) {
        if (!selectedMealKits.containsKey(mealKitId)) {
            throw new MealKitNotInSelectionException(mealKitId);
        }
    }
}
