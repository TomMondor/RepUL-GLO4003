package ca.ulaval.glo4003.repul.delivery.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;

public record CargoPayload(CargoUniqueIdentifier cargoId, String kitchenLocationId, List<MealKitPayload> mealKitsPayload) {
    public static CargoPayload from(Cargo cargo) {
        return new CargoPayload(cargo.getCargoId(), cargo.getKitchenLocation().getLocationId().value(),
            cargo.getMealKits().stream().map(MealKitPayload::from).toList());
    }
}
