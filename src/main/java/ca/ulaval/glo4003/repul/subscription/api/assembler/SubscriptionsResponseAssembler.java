package ca.ulaval.glo4003.repul.subscription.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.subscription.api.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;

public class SubscriptionsResponseAssembler {
    public List<SubscriptionResponse> toSubscriptionsResponse(SubscriptionsPayload subscriptionsPayload) {
        return subscriptionsPayload.subscriptions().stream().map(
            subscriptionPayload -> new SubscriptionResponse(
                subscriptionPayload.subscriptionId().value().toString(),
                subscriptionPayload.frequency().dayOfWeek().name(),
                subscriptionPayload.deliveryLocationId().value(),
                subscriptionPayload.mealKitType().name(),
                subscriptionPayload.startDate().toString(),
                subscriptionPayload.semester().semesterCode().value())
        ).toList();
    }
}