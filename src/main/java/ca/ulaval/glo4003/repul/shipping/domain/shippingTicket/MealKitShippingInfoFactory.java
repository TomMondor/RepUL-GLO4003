package ca.ulaval.glo4003.repul.shipping.domain.shippingTicket;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;

public class MealKitShippingInfoFactory {
    public MealKitShippingInfo createMealKitShippingInfo(DeliveryLocation deliveryLocation, UniqueIdentifier mealKitId) {
        return new MealKitShippingInfo(deliveryLocation, mealKitId, ShippingStatus.PENDING);
    }
}
