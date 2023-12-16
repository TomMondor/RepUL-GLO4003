package ca.ulaval.glo4003.repul.subscription.domain.subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrderFactory;

public class Subscriptions {
    private final Map<SubscriptionUniqueIdentifier, Subscription> subscriptions = new HashMap<>();

    public void add(Subscription subscription) {
        subscriptions.put(subscription.getSubscriptionId(), subscription);
    }

    public Subscription get(SubscriptionUniqueIdentifier subscriptionId) {
        return Optional.ofNullable(subscriptions.get(subscriptionId)).orElseThrow(SubscriptionNotFoundException::new);
    }

    public List<Subscription> getAll() {
        return List.copyOf(subscriptions.values());
    }

    public List<Order> getCurrentOrders() {
        List<Order> currentOrders = new ArrayList<>();
        for (Subscription subscription : subscriptions.values()) {
            if (subscription.isSporadic()) {
                List<Order> subscriptionCurrentOrders = subscription.getCurrentOrders();
                currentOrders.addAll(subscriptionCurrentOrders);
            } else {
                Optional<Order> subscriptionCurrentOrder = subscription.getCurrentOrder();
                subscriptionCurrentOrder.ifPresent(currentOrders::add);
            }
        }
        return currentOrders;
    }

    public Optional<ProcessConfirmationDto> confirm(SubscriptionUniqueIdentifier subscriptionUniqueIdentifier, SubscriberUniqueIdentifier subscriberId,
                                                    OrderFactory orderFactory, PaymentService paymentService) {
        Subscription subscription = get(subscriptionUniqueIdentifier);

        return subscription.confirm(subscriberId, orderFactory, paymentService);
    }

    public void decline(SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = get(subscriptionId);

        subscription.decline();
    }

    public List<ProcessConfirmationDto> processOrders(SubscriberUniqueIdentifier subscriberId, PaymentService paymentService) {
        List<ProcessConfirmationDto> confirmedOrders = new ArrayList<>();
        subscriptions.values().forEach(subscription -> confirmedOrders.add(subscription.processOrders(subscriberId, paymentService)));

        return confirmedOrders;
    }

    public boolean hasOrder(MealKitUniqueIdentifier orderId) {
        return subscriptions.values().stream().anyMatch(subscription -> subscription.hasOrder(orderId));
    }

    public void updateToInDelivery(MealKitUniqueIdentifier orderId) {
        for (Subscription subscription : subscriptions.values()) {
            if (subscription.hasOrder(orderId)) {
                subscription.updateToInDelivery(orderId);
                break;
            }
        }
    }

    public void updateToInPreparation(MealKitUniqueIdentifier orderId) {
        for (Subscription subscription : subscriptions.values()) {
            if (subscription.hasOrder(orderId)) {
                subscription.updateToInPreparation(orderId);
                break;
            }
        }
    }

    public void updateToReadyForPickUp(MealKitUniqueIdentifier orderId) {
        for (Subscription subscription : subscriptions.values()) {
            if (subscription.hasOrder(orderId)) {
                subscription.updateToReadyForPickUp(orderId);
                break;
            }
        }
    }

    public void updateToPickedUp(MealKitUniqueIdentifier orderId) {
        for (Subscription subscription : subscriptions.values()) {
            if (subscription.hasOrder(orderId)) {
                subscription.updateToPickedUp(orderId);
                break;
            }
        }
    }
}
