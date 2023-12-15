package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidCargoIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class Cargos {
    private final Map<CargoUniqueIdentifier, Cargo> cargos;

    public Cargos() {
        this.cargos = new HashMap<>();
    }

    public List<Cargo> findCargosReadyToPickUp() {
        return cargos.values().stream().filter(Cargo::isReadyToBeDelivered).toList();
    }

    public List<Cargo> getAllCargos() {
        return cargos.values().stream().toList();
    }

    public void add(Cargo cargo) {
        cargos.put(cargo.getCargoId(), cargo);
    }

    public Cargo findCargoWithMealKit(MealKitUniqueIdentifier mealKitId) {
        return cargos.values().stream().filter(cargo -> cargo.containsMealKit(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new);
    }

    public Cargo findById(CargoUniqueIdentifier cargoId) {
        return cargos.values().stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new);
    }

    public MealKit extractMealKitFromCargo(MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = findCargoWithMealKit(mealKitId);
        MealKit extractedMealKit = cargo.removeMealKit(mealKitId);

        if (cargo.isEmpty()) {
            cargos.remove(cargo.getCargoId());
        }

        return extractedMealKit;
    }

    public List<MealKit> pickupCargo(CargoUniqueIdentifier cargoId, DeliveryPersonUniqueIdentifier deliveryPersonId) {
        Cargo cargo = findById(cargoId);
        return cargo.pickupCargo(deliveryPersonId);
    }

    public List<MealKit> cancelPickedUpCargo(CargoUniqueIdentifier cargoId, DeliveryPersonUniqueIdentifier deliveryPersonId) {
        Cargo cargo = findById(cargoId);
        return cargo.cancelCargo(deliveryPersonId);
    }

    public void confirmMealKitDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = findById(cargoId);
        cargo.confirmDelivery(deliveryPersonId, mealKitId);
    }

    public MealKit recallMealKitDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = findById(cargoId);
        return cargo.recallDelivery(deliveryPersonId, mealKitId);
    }

    public MealKit getMealKitFromCargo(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = findById(cargoId);
        return cargo.findMealKitById(mealKitId);
    }
}
