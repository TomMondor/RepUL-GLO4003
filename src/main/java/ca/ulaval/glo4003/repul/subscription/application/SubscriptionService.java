package ca.ulaval.glo4003.repul.subscription.application;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionFactory subscriptionFactory;
    private final RepULEventBus eventBus;
    private final PaymentService paymentService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, SubscriptionFactory subscriptionFactory, PaymentService paymentService,
                               RepULEventBus eventBus) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionFactory = subscriptionFactory;
        this.eventBus = eventBus;
        this.paymentService = paymentService;
    }

    public SubscriptionUniqueIdentifier createSubscription(SubscriberUniqueIdentifier subscriberId, DeliveryLocationId deliveryLocationId, DayOfWeek dayOfWeek,
                                                           MealKitType mealKitType) {
        Subscription subscription = subscriptionFactory.createSubscription(subscriberId, deliveryLocationId, dayOfWeek, mealKitType);

        subscriptionRepository.save(subscription);

        return subscription.getSubscriptionId();
    }

    public void orderPickedUpByUser(MealKitUniqueIdentifier mealKitId) {
        Subscription subscription =
            subscriptionRepository.getSubscriptionByOrderId(mealKitId);
        subscription.markOrderAsPickedUp(mealKitId);
        subscriptionRepository.save(subscription);
    }

    public void markMealKitAsToDeliver(List<MealKitDto> mealKits) {
        for (MealKitDto mealKit : mealKits) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(mealKit.mealKitId());
            subscription.markCurrentOrderAsToDeliver();
            subscriptionRepository.save(subscription);
        }
    }

    public void recallMealKitToKitchen(MealKitUniqueIdentifier mealKitId) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(mealKitId);
        subscription.markCurrentOrderAsToCook();
        subscriptionRepository.save(subscription);
    }

    public void markMealKitAsInDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        for (MealKitUniqueIdentifier orderId : mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId);
            subscription.markCurrentOrderAsInDelivery();
            subscriptionRepository.save(subscription);
        }
    }

    public void recallMealKitInDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        for (MealKitUniqueIdentifier orderId : mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId);
            subscription.markCurrentOrderAsToDeliver();
            subscriptionRepository.save(subscription);
        }
    }

    public void confirmMealKitDelivery(MealKitUniqueIdentifier mealKitId) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(mealKitId);
        subscription.markCurrentOrderAsToPickUp();
        subscriptionRepository.save(subscription);
    }

    public void recallMealKitDelivered(MealKitUniqueIdentifier mealKitId) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(mealKitId);
        subscription.markCurrentOrderAsInDelivery();
        subscriptionRepository.save(subscription);
    }

    public SubscriptionsPayload getSubscriptions(SubscriberUniqueIdentifier subscriberId) {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        List<SubscriptionPayload> subscriptionPayloads = subscriptions.stream().map(SubscriptionPayload::from).toList();

        return new SubscriptionsPayload(subscriptionPayloads);
    }

    public SubscriptionPayload getSubscriptionById(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);
        return SubscriptionPayload.from(subscription);
    }

    public void confirmNextMealKitForSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        subscription.confirmNextMealKit();

        subscriptionRepository.save(subscription);
    }

    public void declineNextMealKitForSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        subscription.declineNextMealKit();

        subscriptionRepository.save(subscription);
    }

    private Subscription getSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = subscriptionRepository.getById(subscriptionId);

        if (!subscription.getSubscriberId().equals(subscriberId)) {
            throw new SubscriptionNotFoundException();
        }
        return subscription;
    }

    public OrdersPayload getCurrentOrders(SubscriberUniqueIdentifier subscriberId) {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        List<Order> currentOrders = subscriptions.stream().map(Subscription::findCurrentOrder).filter(Optional::isPresent).map(Optional::get).toList();

        return OrdersPayload.from(currentOrders);
    }

    public void processConfirmationForTheDay() {
        List<Subscription> subscriptions = subscriptionRepository.getAll();
        for (Subscription subscription : subscriptions) {
            processConfirmedOrders(subscription);
            processPendingOrders(subscription);
        }
    }

    private void processConfirmedOrders(Subscription subscription) {
        List<Order> ordersToProcess = subscription.getOrdersReadyToCook();
        for (Order order : ordersToProcess) {
            paymentService.pay(subscription.getSubscriberId(), subscription.getMealKitType(), order.getDeliveryDate());

            sendMealKitConfirmedEvent(subscription, order);

            order.markAsToCook();
        }
    }

    private void processPendingOrders(Subscription subscription) {
        List<Order> ordersToProcess = subscription.getOverdueOrders();
        ordersToProcess.forEach(Order::markAsDeclined);
    }

    private void sendMealKitConfirmedEvent(Subscription subscription, Order confirmedOrder) {
        MealKitConfirmedEvent event = new MealKitConfirmedEvent(
            confirmedOrder.getOrderId(),
            subscription.getSubscriptionId(),
            subscription.getSubscriberId(),
            subscription.getMealKitType(),
            Optional.of(subscription.getDeliveryLocationId()),
            confirmedOrder.getDeliveryDate()
        );

        eventBus.publish(event);
    }
}
