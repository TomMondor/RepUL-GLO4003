package ca.ulaval.glo4003.repul.delivery.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;

public record CargoPayload(String cargoId, String kitchenLocationId, List<MealKitPayload> mealKitsPayload) {
    public static CargoPayload from(Cargo cargo) {
        List<MealKitPayload> mealKitsPayload = cargo.getMealKits().stream().map(MealKitPayload::from).toList();

        return new CargoPayload(cargo.getCargoId().getUUID().toString(), cargo.getKitchenLocation().getLocationId().toString(), mealKitsPayload);
    }
}
