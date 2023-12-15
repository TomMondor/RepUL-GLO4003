package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.time.LocalDate;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;

public class MealKitFactory {
    public MealKit createMealKit(MealKitUniqueIdentifier mealKitId, SubscriptionUniqueIdentifier subscriptionId, SubscriberUniqueIdentifier subscriberId,
                                 MealKitType type, LocalDate deliveryDate, Optional<DeliveryLocationId> deliveryLocationId) {
        boolean isToBeDelivered = deliveryLocationId.isPresent();
        return new MealKit(mealKitId, subscriptionId, subscriberId, deliveryDate, RecipesCatalog.getInstance().getRecipes(type), isToBeDelivered);
    }
}
