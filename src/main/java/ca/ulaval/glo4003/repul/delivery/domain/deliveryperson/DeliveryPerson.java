package ca.ulaval.glo4003.repul.delivery.domain.deliveryperson;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class DeliveryPerson {
    private final DeliveryPersonUniqueIdentifier deliveryPersonId;
    private final DeliveryPersonCargos cargos = new DeliveryPersonCargos();

    public DeliveryPerson(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }

    public List<Cargo> getCargosInPossession() {
        return cargos.getCargosInPossession();
    }

    public List<MealKit> pickUpCargo(Cargo cargo) {
        cargos.pickUpCargo(cargo);
        return cargo.pickUp();
    }

    public MealKit recallMealKitDelivery(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = cargos.findById(cargoId);
        return cargo.recallDelivery(mealKitId);
    }

    public void confirmMealKitDelivery(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = cargos.findById(cargoId);
        cargo.confirmDelivery(mealKitId);
    }

    public MealKit getMealKitFromCargo(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = cargos.findById(cargoId);
        return cargo.findMealKitById(mealKitId);
    }

    public boolean hasCargo(CargoUniqueIdentifier cargoId) {
        return cargos.containsCargo(cargoId);
    }

    public boolean hasMealKit(MealKitUniqueIdentifier mealKitId) {
        return cargos.hasMealKit(mealKitId);
    }

    public MealKit removeMealKitFromCargo(MealKitUniqueIdentifier mealKitId) {
        return cargos.removeMealKitFromCargo(mealKitId);
    }
}
