package ca.ulaval.glo4003.repul.cooking.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitAlreadySelectedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotInSelectionException;

public class Kitchen {
    private final RecipesCatalog recipesCatalog;
    private final KitchenLocationId kitchenLocationId;
    private final Map<UniqueIdentifier, MealKit> mealKits = new HashMap<>();

    public Kitchen(RecipesCatalog recipesCatalog) {
        this.recipesCatalog = recipesCatalog;
        kitchenLocationId = new KitchenLocationId("DESJARDINS");
    }

    public KitchenLocationId getKitchenLocationId() {
        return kitchenLocationId;
    }

    public void addMealKit(UniqueIdentifier mealKitId, MealKitType type, LocalDate deliveryDate) {
        MealKit mealKit = new MealKit(mealKitId, deliveryDate, recipesCatalog.getRecipes(type));
        mealKits.put(mealKitId, mealKit);
    }

    public List<MealKit> getMealKitsToCook() {
        return mealKits.values().stream().filter(mealKit -> mealKit.isUnselected() && !mealKit.isCooked() && mealKit.isDeliveryTomorrow()).toList();
    }

    public void select(UniqueIdentifier cookId, List<UniqueIdentifier> selectedMealKitIds) {
        verifyMealKitsAreUnassigned(selectedMealKitIds);

        selectedMealKitIds.forEach(id -> {
            MealKit mealKit = getMealKit(id);
            mealKit.selectBy(cookId);
        });
    }

    public List<UniqueIdentifier> getSelection(UniqueIdentifier cookId) {
        return getSelectedMealKits(cookId).stream().map(MealKit::getMealKitId).toList();
    }

    public void cancelOneSelected(UniqueIdentifier cookId, UniqueIdentifier mealKitId) {
        verifyMealKitsInCookSelection(cookId, List.of(mealKitId));
        getMealKit(mealKitId).unselect();
    }

    public void cancelAllSelected(UniqueIdentifier cookId) {
        unselectSelectedMealKits(cookId);
    }

    public void confirmCooked(UniqueIdentifier cookId, UniqueIdentifier mealKitId) {
        verifyMealKitsInCookSelection(cookId, List.of(mealKitId));
        getMealKit(mealKitId).setCooked();
    }

    public void confirmCooked(UniqueIdentifier cookId, List<UniqueIdentifier> mealKitIds) {
        verifyMealKitsInCookSelection(cookId, mealKitIds);
        mealKitIds.forEach(id -> getMealKit(id).setCooked());
    }

    public void recallCooked(UniqueIdentifier cookId, UniqueIdentifier mealKitId) {
        MealKit mealKit = getMealKit(mealKitId);
        mealKit.recallMealKit(cookId);
    }

    public void removeMealKitsFromKitchen(List<UniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(mealKits::remove);
    }

    private void verifyMealKitsAreUnassigned(List<UniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(this::verifyMealKitIsUnassigned);
    }

    private void verifyMealKitIsUnassigned(UniqueIdentifier mealKitId) {
        MealKit mealKit = getMealKit(mealKitId);
        if (!mealKit.isUnselected()) {
            throw new MealKitAlreadySelectedException(mealKitId);
        }
    }

    private void verifyMealKitsInCookSelection(UniqueIdentifier cookId, List<UniqueIdentifier> mealKitIds) {
        List<UniqueIdentifier> cookSelection = getSelection(cookId);
        mealKitIds.forEach(mealKitId -> {
            if (!cookSelection.contains(mealKitId)) {
                throw new MealKitNotInSelectionException(mealKitId);
            }
        });
    }

    private void unselectSelectedMealKits(UniqueIdentifier cookId) {
        getSelectedMealKits(cookId).forEach(MealKit::unselect);
    }

    private MealKit getMealKit(UniqueIdentifier mealKitId) {
        MealKit mealKit = mealKits.get(mealKitId);
        if (mealKit == null) {
            throw new MealKitNotFoundException(mealKitId);
        }
        return mealKit;
    }

    private List<MealKit> getSelectedMealKits(UniqueIdentifier cookId) {
        return mealKits.values().stream().filter(mealKit -> mealKit.isSelectedBy(cookId)).toList();
    }
}
