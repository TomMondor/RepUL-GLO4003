package ca.ulaval.glo4003.repul.subscription.domain;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface SubscriptionRepository {
    void save(Subscription subscription);

    Subscription getById(UniqueIdentifier subscriptionId);

    List<Subscription> getBySubscriberId(UniqueIdentifier subscriberId);

    Subscription getSubscriptionByOrderId(UniqueIdentifier orderId);
}
