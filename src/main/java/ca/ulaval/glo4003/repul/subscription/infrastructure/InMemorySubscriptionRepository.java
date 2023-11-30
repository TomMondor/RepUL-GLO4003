package ca.ulaval.glo4003.repul.subscription.infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.application.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public class InMemorySubscriptionRepository implements SubscriptionRepository {
    private final Map<UniqueIdentifier, Subscription> subscriptionsById = new HashMap<>();

    @Override
    public void save(Subscription subscription) {
        subscriptionsById.put(subscription.getSubscriptionId(), subscription);
    }

    @Override
    public Subscription getById(UniqueIdentifier subscriptionId) {
        Subscription subscription = subscriptionsById.get(subscriptionId);
        if (subscription == null) {
            throw new SubscriptionNotFoundException();
        }
        return subscription;
    }

    @Override
    public List<Subscription> getBySubscriberId(UniqueIdentifier subscriberId) {
        return subscriptionsById.values().stream().filter(subscription -> subscription.getSubscriberId().equals(subscriberId)).toList();
    }

    @Override
    public Subscription getSubscriptionByOrderId(UniqueIdentifier orderId) {
        for (Subscription subscription : subscriptionsById.values()) {
            Optional<Order> order = subscription.findCurrentOrder();
            if (order.isPresent() && order.get().getOrderId().equals(orderId)) {
                return subscription;
            }
        }
        throw new OrderNotFoundException();
    }
}
