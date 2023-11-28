package ca.ulaval.glo4003.repul.subscription.domain;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface SubscriptionRepository {
    void save(Subscription subscription);

    Optional<Subscription> getById(UniqueIdentifier subscriptionId);

    List<Subscription> getBySubscriberId(UniqueIdentifier subscriberId);

    Optional<Subscription> getSubscriptionByOrderId(UniqueIdentifier orderId);
}
