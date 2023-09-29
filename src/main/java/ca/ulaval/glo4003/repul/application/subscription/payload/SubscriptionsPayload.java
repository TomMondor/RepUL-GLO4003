package ca.ulaval.glo4003.repul.application.subscription.payload;

import java.util.List;

public record SubscriptionsPayload(List<SubscriptionPayload> subscriptions) {
}
