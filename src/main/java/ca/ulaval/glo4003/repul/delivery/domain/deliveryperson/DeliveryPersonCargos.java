package ca.ulaval.glo4003.repul.delivery.domain.deliveryperson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class DeliveryPersonCargos {
    private final Map<CargoUniqueIdentifier, Cargo> cargos = new HashMap<>();

    public List<Cargo> getCargosInPossession() {
        return cargos.values().stream().toList();
    }

    public void pickUpCargo(Cargo cargo) {
        cargos.put(cargo.getCargoId(), cargo);
    }

    public Cargo findById(CargoUniqueIdentifier cargoId) {
        if (!cargos.containsKey(cargoId)) {
            throw new CargoNotFoundException();
        }
        return cargos.get(cargoId);
    }

    public boolean hasMealKit(MealKitUniqueIdentifier mealKitId) {
        return cargos.values().stream().anyMatch(cargo -> cargo.containsMealKit(mealKitId));
    }

    public MealKit removeMealKitFromCargo(MealKitUniqueIdentifier mealKitId) {
        Cargo cargo = findCargoWithMealKit(mealKitId);
        MealKit extractedMealKit = cargo.removeMealKit(mealKitId);

        if (cargo.isEmpty()) {
            cargos.remove(cargo.getCargoId());
        }

        return extractedMealKit;
    }

    public boolean containsCargo(CargoUniqueIdentifier cargoId) {
        return cargos.containsKey(cargoId);
    }

    private Cargo findCargoWithMealKit(MealKitUniqueIdentifier mealKitId) {
        return cargos.values().stream().filter(cargo -> cargo.containsMealKit(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new);
    }
}
