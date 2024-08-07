package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class CargoFactory {
    public Cargo createCargo(KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        return new Cargo(new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate(), kitchenLocation, mealKits);
    }
}
