package ca.ulaval.glo4003.repul.application.subscription.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;

public record SubscriptionsPayload(List<SubscriptionPayload> subscriptions) {
    public static SubscriptionsPayload from(List<Subscription> subscriptions) {
        return new SubscriptionsPayload(subscriptions.stream().map(SubscriptionPayload::from).toList());
    }
}
