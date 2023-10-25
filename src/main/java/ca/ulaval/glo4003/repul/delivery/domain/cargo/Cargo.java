package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.List;

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
        // TODO : Ajouter une vérification que chaque meal kit dans le cargo est dans l'état IN_DELIVERY
        if (!deliveryPersonId.equals(this.deliveryPersonId)) {
            throw new InvalidDeliveryPersonIdException();
        }
        this.deliveryPersonId = null;
        mealKits.forEach(MealKit::cancelDelivery);
    }

    public void confirmDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier mealKitId) {
        if (!deliveryPersonId.equals(this.deliveryPersonId)) {
            throw new InvalidDeliveryPersonIdException();
        }
        mealKits.stream().filter(mealKit -> mealKit.getMealKitId().equals(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new).confirmDelivery();
    }

    public LockerId recallDelivery(UniqueIdentifier deliveryPersonId, UniqueIdentifier mealKitId) {
        if (!deliveryPersonId.equals(this.deliveryPersonId)) {
            throw new InvalidDeliveryPersonIdException();
        }
        return mealKits.stream().filter(mealKit -> mealKit.getMealKitId().equals(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new)
            .recallDelivery();
    }
}
