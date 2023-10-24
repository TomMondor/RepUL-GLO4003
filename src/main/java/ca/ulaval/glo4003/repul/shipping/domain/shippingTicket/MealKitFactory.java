package ca.ulaval.glo4003.repul.shipping.domain.shippingTicket;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;

public class MealKitFactory {
    public MealKit createMealKit(DeliveryLocation deliveryLocation, UniqueIdentifier mealKitId) {
        return new MealKit(deliveryLocation, mealKitId, ShippingStatus.PENDING);
    }
}
