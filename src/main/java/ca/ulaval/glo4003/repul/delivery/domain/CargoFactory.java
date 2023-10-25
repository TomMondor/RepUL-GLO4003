package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.List;
import java.util.UUID;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public class CargoFactory {
    public Cargo createCargo(KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        return new Cargo(new UniqueIdentifier(UUID.randomUUID()), kitchenLocation, mealKits);
    }
}
