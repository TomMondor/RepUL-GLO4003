package ca.ulaval.glo4003.repul.subscription.application;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
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

        subscriptionRepository.save(subscription);

        return subscription.getSubscriptionId();
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(MealKitPickedUpByUserEvent event) {
        Subscription subscription =
            subscriptionRepository.getSubscriptionByOrderId(event.mealKitId).orElseThrow(OrderNotFoundException::new);
        subscription.markOrderAsPickedUp(event.mealKitId);
        subscriptionRepository.save(subscription);
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent event) {
        for (UniqueIdentifier orderId : event.mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId).orElseThrow(OrderNotFoundException::new);
            subscription.markCurrentOrderAsToDeliver();
            subscriptionRepository.save(subscription);
        }
    }

    @Subscribe
    public void handleRecallCookedMealKitEvent(RecallCookedMealKitEvent recallCookedMealKitEvent) {
        Subscription subscription =
            subscriptionRepository.getSubscriptionByOrderId(recallCookedMealKitEvent.mealKitId).orElseThrow(OrderNotFoundException::new);
        subscription.markCurrentOrderAsToCook();
        subscriptionRepository.save(subscription);
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        for (UniqueIdentifier orderId : event.mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId).orElseThrow(OrderNotFoundException::new);
            subscription.markCurrentOrderAsInDelivery();
            subscriptionRepository.save(subscription);
        }
    }

    @Subscribe
    public void handleCanceledCargoEvent(CanceledCargoEvent event) {
        for (UniqueIdentifier orderId : event.mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId).orElseThrow(OrderNotFoundException::new);
            subscription.markCurrentOrderAsToDeliver();
            subscriptionRepository.save(subscription);
        }
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent event) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(event.mealKitId).orElseThrow(OrderNotFoundException::new);
        subscription.markCurrentOrderAsToPickUp();
        subscriptionRepository.save(subscription);
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent event) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(event.mealKitId).orElseThrow(OrderNotFoundException::new);
        subscription.markCurrentOrderAsInDelivery();
        subscriptionRepository.save(subscription);
    }

    public SubscriptionsPayload getSubscriptions(UniqueIdentifier subscriberId) {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        List<SubscriptionPayload> subscriptionPayloads = subscriptions.stream().map(SubscriptionPayload::from).toList();

        return new SubscriptionsPayload(subscriptionPayloads);
    }

    public SubscriptionPayload getSubscriptionById(UniqueIdentifier subscriberId, UniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);
        return SubscriptionPayload.from(subscription);
    }

    public void confirmNextMealKitForSubscription(UniqueIdentifier subscriberId, UniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        Order confirmedOrder = subscription.confirmNextMealKit();

        subscriptionRepository.save(subscription);

        eventBus.publish(new MealKitConfirmedEvent(confirmedOrder.getOrderId(), subscription.getSubscriptionId(), subscription.getSubscriberId(),
            subscription.getMealKitType(), subscription.getDeliveryLocationId(), confirmedOrder.getDeliveryDate()));
    }

    public void declineNextMealKitForSubscription(UniqueIdentifier subscriberId, UniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        subscription.declineNextMealKit();

        subscriptionRepository.save(subscription);
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
