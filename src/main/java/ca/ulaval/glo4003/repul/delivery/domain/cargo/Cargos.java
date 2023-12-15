package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class Cargos {
    private final Map<CargoUniqueIdentifier, Cargo> cargos = new HashMap<>();
    ;

    public List<Cargo> findCargosReadyToPickUp() {
        return cargos.values().stream().toList();
    }

    public void addCargoReadyToPickUp(Cargo cargo) {
        cargos.put(cargo.getCargoId(), cargo);
    }

    public MealKit extractMealKitFromCargo(MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = findCargoWithMealKit(mealKitId);
        MealKit extractedMealKit = cargo.removeMealKit(mealKitId);

        if (cargo.isEmpty()) {
            cargos.remove(cargo.getCargoId());
        }

        return extractedMealKit;
    }

    public Cargo pickUpCargo(CargoUniqueIdentifier cargoId) {
        if (!cargos.containsKey(cargoId)) {
            throw new CargoNotFoundException();
        }
        return cargos.remove(cargoId);
    }

    private Cargo findCargoWithMealKit(MealKitUniqueIdentifier mealKitId) {
        return cargos.values().stream().filter(cargo -> cargo.containsMealKit(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new);
    }
}
