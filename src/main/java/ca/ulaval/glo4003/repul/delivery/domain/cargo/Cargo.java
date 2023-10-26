package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidDeliveryPersonIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;

public class Cargo {
    private final UniqueIdentifier cargoId;
    private final List<MealKit> mealKits;
    private final KitchenLocation kitchenLocation;
    private UniqueIdentifier deliveryPersonId;

    public Cargo(UniqueIdentifier cargoId, KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        this.cargoId = cargoId;
        this.kitchenLocation = kitchenLocation;
        this.mealKits = mealKits;
    }

    public UniqueIdentifier getCargoId() {
        return cargoId;
    }

    public UniqueIdentifier getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public KitchenLocation getKitchenLocation() {
        return kitchenLocation;
    }

    public List<MealKit> getMealKits() {
        return mealKits;
    }

    public List<MealKit> pickupCargo(UniqueIdentifier deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
        mealKits.forEach(MealKit::pickupReadyToDeliverMealKit);
        return mealKits;
    }

    public void cancelCargo(UniqueIdentifier deliveryPersonId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);

        this.deliveryPersonId = null;

        mealKits.stream().filter(MealKit::isNotAlreadyDelivered).forEach(MealKit::cancelDelivery);
    }

    public void confirmDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier mealKitId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);
        mealKits.stream().filter(mealKit -> mealKit.getMealKitId().equals(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new).confirmDelivery();
    }

    public Optional<LockerId> recallDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier mealKitId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);
        return mealKits.stream().filter(mealKit -> mealKit.getMealKitId().equals(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new)
            .recallDelivery();
    }

    public boolean containsMealKit(UniqueIdentifier mealKitId) {
        return mealKits.stream().anyMatch(mealKit -> mealKit.getMealKitId().equals(mealKitId));
    }

    public MealKit removeMealKit(UniqueIdentifier mealKitId) {
        MealKit mealKit =
            mealKits.stream().filter(currentMealKit -> currentMealKit.getMealKitId().equals(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new);

        mealKits.remove(mealKit);

        return mealKit;
    }

    public boolean isEmpty() {
        return mealKits.isEmpty();
    }

    private void validateIsSameDeliveryPersonId(UniqueIdentifier deliveryPersonId) {
        if (!deliveryPersonId.equals(this.deliveryPersonId)) {
            throw new InvalidDeliveryPersonIdException();
        }
    }
}
