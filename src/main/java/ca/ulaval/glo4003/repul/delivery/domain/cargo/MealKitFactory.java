package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class MealKitFactory {
    public MealKit createMealKit(DeliveryLocationId deliveryLocationId, MealKitUniqueIdentifier mealKitId) {
        return new MealKit(deliveryLocationId, mealKitId, DeliveryStatus.PENDING);
    }
}
