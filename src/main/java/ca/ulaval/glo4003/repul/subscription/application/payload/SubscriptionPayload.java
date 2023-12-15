package ca.ulaval.glo4003.repul.subscription.application.payload;

import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;

public record SubscriptionPayload(
    String subscriptionId,
    String frequency,
    String deliveryLocationId,
    String startDate,
    String mealKitType,
    String semester
) {

    public static SubscriptionPayload from(Subscription subscription) {
        String frequency = (subscription.getFrequency().isPresent()) ?
            (subscription.getFrequency().get().dayOfWeek().toString()) : "Sporadic";
        String deliveryLocationId = (subscription.getDeliveryLocationId().isPresent()) ?
            (subscription.getDeliveryLocationId().get().toString()) : "In kitchen pick up";

        return new SubscriptionPayload(
            subscription.getSubscriptionId().getUUID().toString(),
            frequency,
            deliveryLocationId,
            subscription.getStartDate().toString(),
            subscription.getMealKitType().toString(),
            subscription.getSemester().toString()
        );
    }
}
