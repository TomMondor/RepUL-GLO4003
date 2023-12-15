package ca.ulaval.glo4003.repul.delivery.domain.mealkit;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;

public class MealKitFactory {
    public MealKit createMealKit(SubscriberUniqueIdentifier subscriberId,
                                 SubscriptionUniqueIdentifier subscriptionId,
                                 MealKitUniqueIdentifier mealKitId,
                                 DeliveryLocationId deliveryLocationId) {
        return new MealKit(subscriberId, subscriptionId, mealKitId, deliveryLocationId, DeliveryStatus.PENDING);
    }
}
