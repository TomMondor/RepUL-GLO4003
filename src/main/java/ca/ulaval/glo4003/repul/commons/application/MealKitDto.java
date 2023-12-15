package ca.ulaval.glo4003.repul.commons.application;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;

public record MealKitDto(
    SubscriberUniqueIdentifier subscriberId,
    SubscriptionUniqueIdentifier subscriptionId,
    MealKitUniqueIdentifier mealKitId
) {
}
