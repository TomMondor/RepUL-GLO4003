package ca.ulaval.glo4003.repul.api.subscription.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionsPayload;

public class SubscriptionsResponseAssembler {
    public List<SubscriptionResponse> toSubscriptionsResponse(SubscriptionsPayload subscriptionsPayload) {
        return subscriptionsPayload.subscriptions().stream().map(
            subscriptionPayload -> new SubscriptionResponse(
                subscriptionPayload.subscriptionId().value().toString(),
                subscriptionPayload.frequency().dayOfWeek().name(),
                subscriptionPayload.locationPayload().locationId().value(),
                subscriptionPayload.lunchboxType().name(),
                subscriptionPayload.startDate().toString())).toList();
    }
}
