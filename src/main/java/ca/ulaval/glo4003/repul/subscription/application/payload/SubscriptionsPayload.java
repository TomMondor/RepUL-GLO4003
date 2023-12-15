package ca.ulaval.glo4003.repul.subscription.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;

public record SubscriptionsPayload(List<SubscriptionPayload> subscriptions) {
    public static SubscriptionsPayload from(List<Subscription> subscriptions) {
        return new SubscriptionsPayload(subscriptions.stream().map(SubscriptionPayload::from).toList());
    }
}
