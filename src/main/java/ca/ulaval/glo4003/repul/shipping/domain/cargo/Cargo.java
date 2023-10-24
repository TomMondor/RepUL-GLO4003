package ca.ulaval.glo4003.repul.shipping.domain.cargo;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShipperException;

public class Cargo {
    private final UniqueIdentifier cargoId;
    private final List<MealKit> mealKits;
    private KitchenLocation kitchenLocation;
    private UniqueIdentifier shipperId;

    public Cargo(UniqueIdentifier cargoId, KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        this.cargoId = cargoId;
        this.kitchenLocation = kitchenLocation;
        this.mealKits = mealKits;
    }

    public UniqueIdentifier getCargoId() {
        return cargoId;
    }

    public UniqueIdentifier getShipperId() {
        return shipperId;
    }

    public KitchenLocation getPickupLocation() {
        return kitchenLocation;
    }

    public List<MealKit> getMealKits() {
        return mealKits;
    }

    public List<MealKit> pickupCargo(UniqueIdentifier accountId) {
        this.shipperId = accountId;
        mealKits.forEach(MealKit::pickupReadyToDeliverMealKit);
        return mealKits;
    }

    public void cancelShipping(UniqueIdentifier accountId) {
        if (!accountId.equals(shipperId)) {
            throw new InvalidShipperException();
        }
        this.shipperId = null;
        mealKits.forEach(MealKit::cancelInDeliveryShipping);
    }

    public void confirmShipping(UniqueIdentifier accountId, UniqueIdentifier mealKitId) {
        if (!accountId.equals(shipperId)) {
            throw new InvalidShipperException();
        }
        mealKits.stream()
            .filter(mealKit -> mealKit.getMealKitId().equals(mealKitId))
            .findFirst()
            .orElseThrow(InvalidMealKitIdException::new)
            .confirmShipping();
    }

    public LockerId unconfirmShipping(UniqueIdentifier accountId, UniqueIdentifier mealKitId) {
        if (!accountId.equals(shipperId)) {
            throw new InvalidShipperException();
        }
        return mealKits.stream()
            .filter(mealKit -> mealKit.getMealKitId().equals(mealKitId))
            .findFirst()
            .orElseThrow(InvalidMealKitIdException::new)
            .unconfirmShipping();
    }
}
