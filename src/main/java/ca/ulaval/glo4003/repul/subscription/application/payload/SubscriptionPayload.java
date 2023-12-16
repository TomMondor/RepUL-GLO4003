package ca.ulaval.glo4003.repul.subscription.application.payload;

import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;

public record SubscriptionPayload(
    String subscriptionId,
    String weeklyOccurence,
    String deliveryLocationId,
    String startDate,
    String mealKitType,
    String subscriptionType,
    String semester
) {

    public static SubscriptionPayload from(Subscription subscription) {
        String weeklyOccurence = (subscription.getWeeklyOccurence().isPresent()) ?
            (subscription.getWeeklyOccurence().get().dayOfWeek().toString()) : "Sporadic";
        String deliveryLocationId = (subscription.getDeliveryLocationId().isPresent()) ?
            (subscription.getDeliveryLocationId().get().toString()) : "In kitchen pick up";
        String subscriptionType = (subscription.isSporadic()) ? "SPORADIC" : "WEEKLY";

        return new SubscriptionPayload(
            subscription.getSubscriptionId().getUUID().toString(),
            weeklyOccurence,
            deliveryLocationId,
            subscription.getStartDate().toString(),
            subscription.getMealKitType().toString(),
            subscriptionType,
            subscription.getSemester().toString()
        );
    }
}
