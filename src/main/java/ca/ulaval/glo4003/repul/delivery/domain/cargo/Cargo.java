package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoAlreadyPickedUpException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidDeliveryPersonIdException;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKits;

public class Cargo {
    private final CargoUniqueIdentifier cargoId;
    private final MealKits mealKits;
    private final KitchenLocation kitchenLocation;
    private Optional<DeliveryPersonUniqueIdentifier> deliveryPersonId = Optional.empty();

    public Cargo(CargoUniqueIdentifier cargoId, KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        this.cargoId = cargoId;
        this.kitchenLocation = kitchenLocation;
        this.mealKits = new MealKits(mealKits);
    }

    public CargoUniqueIdentifier getCargoId() {
        return cargoId;
    }

    public Optional<DeliveryPersonUniqueIdentifier> getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public KitchenLocation getKitchenLocation() {
        return kitchenLocation;
    }

    public List<MealKit> getMealKits() {
        return mealKits.getAllMealKits();
    }

    public boolean isReadyToBeDelivered() {
        return deliveryPersonId.isEmpty();
    }

    public List<MealKit> pickupCargo(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        if (this.deliveryPersonId.isPresent()) {
            throw new CargoAlreadyPickedUpException();
        }
        this.deliveryPersonId = Optional.of(deliveryPersonId);
        mealKits.markMealKitsAsPickedUp();
        return mealKits.getAllMealKits();
    }

    public List<MealKit> cancelCargo(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);

        this.deliveryPersonId = Optional.empty();

        List<MealKit> notYetDeliveredMealKits = mealKits.getMealKitsNotDelivered();
        notYetDeliveredMealKits.forEach(MealKit::markAsReadyToBeDelivered);

        return notYetDeliveredMealKits;
    }

    public void confirmDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, MealKitUniqueIdentifier mealKitId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);

        mealKits.confirmDelivery(mealKitId);
    }

    public MealKit recallDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, MealKitUniqueIdentifier mealKitId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);

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

    private void validateIsSameDeliveryPersonId(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        if (!Optional.of(deliveryPersonId).equals(this.deliveryPersonId)) {
            throw new InvalidDeliveryPersonIdException();
        }
    }

    public MealKit findMealKitById(MealKitUniqueIdentifier mealKitId) {
        return mealKits.findById(mealKitId);
    }
}
