package ca.ulaval.glo4003.repul.cooking.domain.Cook;

import java.util.Arrays;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;

public class Cook {
    private final CookUniqueIdentifier id;
    private final CookSelectedMealKits selectedMealKits;

    public Cook(CookUniqueIdentifier id) {
        this.id = id;
        this.selectedMealKits = new CookSelectedMealKits();
    }

    public CookUniqueIdentifier getId() {
        return id;
    }

    public List<MealKitUniqueIdentifier> getSelectedMealKitsIds() {
        return selectedMealKits.getIds();
    }

    public void selectMealKit(MealKit mealKit) {
        selectedMealKits.selectMealKits(Arrays.asList(mealKit));
    }

    public void selectMealKits(List<MealKit> newSelectedMealKits) {
        selectedMealKits.selectMealKits(newSelectedMealKits);
    }

    public MealKit unselectMealKit(MealKitUniqueIdentifier mealKitId) {
        return selectedMealKits.unselectMealKits(Arrays.asList(mealKitId)).get(0);
    }

    public List<MealKit> unselectMealKits(List<MealKitUniqueIdentifier> mealKitIds) {
        return selectedMealKits.unselectMealKits(mealKitIds);
    }

    public List<MealKit> unselectAllMealKits() {
        return selectedMealKits.unselectMealKits(selectedMealKits.getIds());
    }

    public MealKit confirmMealKitAssembled(MealKitUniqueIdentifier mealKitId) {
        return selectedMealKits.confirmMealKitAssembled(mealKitId);
    }

    public List<MealKit> confirmMealKitsCooked(List<MealKitUniqueIdentifier> mealKitIds) {
        return selectedMealKits.confirmMealKitsCooked(mealKitIds);
    }
}
