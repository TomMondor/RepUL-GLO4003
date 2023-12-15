package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKits;

public class Cargo {
    private final CargoUniqueIdentifier cargoId;
    private final MealKits mealKits;
    private final KitchenLocation kitchenLocation;

    public Cargo(CargoUniqueIdentifier cargoId, KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        this.cargoId = cargoId;
        this.kitchenLocation = kitchenLocation;
        this.mealKits = new MealKits(mealKits);
    }

    public CargoUniqueIdentifier getCargoId() {
        return cargoId;
    }

    public KitchenLocation getKitchenLocation() {
        return kitchenLocation;
    }

    public List<MealKit> getMealKits() {
        return mealKits.getAllMealKits();
    }

    public List<MealKit> pickUp() {
        mealKits.markMealKitsAsPickedUp();
        return mealKits.getAllMealKits();
    }

    public void confirmDelivery(MealKitUniqueIdentifier mealKitId) {
        mealKits.confirmDelivery(mealKitId);
    }

    public MealKit recallDelivery(MealKitUniqueIdentifier mealKitId) {
        return mealKits.recallDeliveredMealKit(mealKitId);
    }

    public boolean containsMealKit(MealKitUniqueIdentifier mealKitId) {
        return mealKits.containsMealKit(mealKitId);
    }

    public MealKit removeMealKit(MealKitUniqueIdentifier mealKitId) {
        return mealKits.removeMealKit(mealKitId);
    }

    public boolean isEmpty() {
        return mealKits.isEmpty();
    }

    public MealKit findMealKitById(MealKitUniqueIdentifier mealKitId) {
        return mealKits.findById(mealKitId);
    }
}
