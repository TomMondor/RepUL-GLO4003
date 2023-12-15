package ca.ulaval.glo4003.repul.subscription.domain.query;

import java.time.DayOfWeek;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DateParser;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.api.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionType;

public record SubscriptionQuery(
    SubscriptionType subscriptionType,
    SubscriberUniqueIdentifier subscriberId,
    Optional<DeliveryLocationId> locationId,
    Optional<DayOfWeek> dayOfWeek,
    Optional<MealKitType> mealKitType
) {
    public static SubscriptionQuery from(SubscriptionRequest subscriptionRequest, String subscriberId) {
        return new SubscriptionQuery(SubscriptionType.from(subscriptionRequest.subscriptionType),
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(subscriberId),
            subscriptionRequest.locationId == null ? Optional.empty() : Optional.of(
                DeliveryLocationId.from(subscriptionRequest.locationId)),
            subscriptionRequest.dayOfWeek == null ? Optional.empty() : Optional.of(
                DateParser.dayOfWeekFrom(subscriptionRequest.dayOfWeek)),
            subscriptionRequest.mealKitType == null ? Optional.empty() : Optional.of(
                MealKitType.from(subscriptionRequest.mealKitType)));
    }
}
