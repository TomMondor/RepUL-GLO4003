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
        return Optional.ofNullable(subscriptions.get(subscriptionId)).orElseThrow(() -> new SubscriptionNotFoundException());
    }

    public List<Subscription> getAll() {
        return List.copyOf(subscriptions.values());
    }

    public List<Order> getCurrentOrders() {
        return subscriptions.values().stream().map(Subscription::getCurrentOrder).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public Optional<ProcessConfirmation> confirm(SubscriptionUniqueIdentifier subscriptionUniqueIdentifier, SubscriberUniqueIdentifier subscriberId,
                                                 OrderFactory orderFactory, PaymentService paymentService) {
        Subscription subscription = get(subscriptionUniqueIdentifier);

        return subscription.confirm(subscriberId, orderFactory, paymentService);
    }

    public void decline(SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = get(subscriptionId);

        subscription.decline();
    }

    public List<ProcessConfirmation> processOrders(SubscriberUniqueIdentifier subscriberId, PaymentService paymentService) {
        List<ProcessConfirmation> confirmedOrders = new ArrayList<>();
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

    public void updateToReadyForPickup(MealKitUniqueIdentifier orderId) {
        for (Subscription subscription : subscriptions.values()) {
            if (subscription.hasOrder(orderId)) {
                subscription.updateToReadyForPickup(orderId);
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
