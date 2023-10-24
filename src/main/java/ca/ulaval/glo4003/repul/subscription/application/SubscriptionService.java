package ca.ulaval.glo4003.repul.subscription.application;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.shipping.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

import com.google.common.eventbus.Subscribe;

public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionFactory subscriptionFactory;
    private final RepULEventBus eventBus;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, SubscriptionFactory subscriptionFactory,
                               RepULEventBus eventBus) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionFactory = subscriptionFactory;
        this.eventBus = eventBus;
    }

    public UniqueIdentifier createSubscription(UniqueIdentifier subscriberId, SubscriptionQuery subscriptionQuery) {
        Subscription subscription = subscriptionFactory.createSubscription(
            subscriberId,
            subscriptionQuery.deliveryLocationId(),
            subscriptionQuery.dayOfWeek(),
            subscriptionQuery.mealKitType());

        subscriptionRepository.saveOrUpdate(subscription);

        return subscription.getSubscriptionId();
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent event) {
        for (UniqueIdentifier orderId : event.mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId).orElseThrow(OrderNotFoundException::new);
            subscription.markAsCooked();
            subscriptionRepository.saveOrUpdate(subscription);
        }
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        for (UniqueIdentifier orderId : event.mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId).orElseThrow(OrderNotFoundException::new);
            subscription.markAsInDelivery();
            subscriptionRepository.saveOrUpdate(subscription);
        }
    }

    public SubscriptionsPayload getSubscriptions(UniqueIdentifier subscriberId) {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        List<SubscriptionPayload> subscriptionPayloads = subscriptions.stream().map(SubscriptionPayload::from).toList();

        return new SubscriptionsPayload(subscriptionPayloads);
    }

    public void confirmNextMealKitForSubscription(UniqueIdentifier subscriberId, UniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        Order confirmedOrder = subscription.confirmNextMealKit();
        eventBus.publish(new MealKitConfirmedEvent(confirmedOrder.getOrderId(), subscription.getSubscriptionId(), subscription.getSubscriberId(),
            subscription.getMealKitType(), subscription.getDeliveryLocationId(), confirmedOrder.getDeliveryDate()));

        subscriptionRepository.saveOrUpdate(subscription);
    }

    public void declineNextMealKitForSubscription(UniqueIdentifier subscriberId, UniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        subscription.declineNextMealKit();

        subscriptionRepository.saveOrUpdate(subscription);
    }

    private Subscription getSubscription(UniqueIdentifier subscriberId, UniqueIdentifier subscriptionId) {
        Optional<Subscription> optionalSubscription = subscriptionRepository.getById(subscriptionId);
        if (optionalSubscription.isEmpty()) {
            throw new SubscriptionNotFoundException();
        }
        Subscription subscription = optionalSubscription.get();
        if (!subscription.getSubscriberId().equals(subscriberId)) {
            throw new SubscriptionNotFoundException();
        }
        return subscription;
    }

    public OrdersPayload getCurrentOrders(UniqueIdentifier subscriberId) {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        List<Order> currentOrders = subscriptions.stream()
            .map(Subscription::findCurrentOrder)
            .filter(Optional::isPresent)
            .map(Optional::get).toList();

        return OrdersPayload.from(currentOrders);
    }
}