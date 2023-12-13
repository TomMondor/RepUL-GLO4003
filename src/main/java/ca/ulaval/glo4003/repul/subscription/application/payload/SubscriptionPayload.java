package ca.ulaval.glo4003.repul.subscription.application.payload;

import ca.ulaval.glo4003.repul.subscription.domain.Subscription;

public record SubscriptionPayload(
    String subscriptionId,
    String frequency,
    String deliveryLocationId,
    String startDate,
    String mealKitType,
    String semester
) {

    public static SubscriptionPayload from(Subscription subscription) {
        return new SubscriptionPayload(
            subscription.getSubscriptionId().getUUID().toString(),
            subscription.getFrequency().dayOfWeek().toString(),
            subscription.getDeliveryLocationId().toString(),
            subscription.getStartDate().toString(),
            subscription.getMealKitType().toString(),
            subscription.getSemester().toString()
        );
    }
}
