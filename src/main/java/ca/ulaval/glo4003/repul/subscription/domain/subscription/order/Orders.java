package ca.ulaval.glo4003.repul.subscription.domain.subscription.order;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoUpcomingOrderInSubscriptionException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.InDeliveryStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.InPreparationStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.PickedUpStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.ReadyForPickUpStatus;

public class Orders {
    private final List<Order> orders = new LinkedList<>();

    public Orders() {
    }

    public Orders(List<Order> orders) {
        this.orders.addAll(orders);
    }

    public void add(Order order) {
        orders.add(order);
    }

    public Optional<Order> getCurrent() {
        return orders.stream().filter(Order::isActive).findFirst();
    }

    public List<Order> getCurrents() {
        return orders.stream().filter(Order::isActive).toList();
    }

    public List<Order> getAll() {
        return List.copyOf(orders);
    }

    public void confirmCurrent() {
        Order currentOrder = getCurrent().orElseThrow(() -> new NoUpcomingOrderInSubscriptionException());

        currentOrder.confirm();
    }

    public void declineCurrent() {
        Order currentOrder = getCurrent().orElseThrow(() -> new NoUpcomingOrderInSubscriptionException());

        currentOrder.decline();
    }

    public List<Order> process(SubscriberUniqueIdentifier subscriberId, boolean isSporadic, PaymentService paymentService) {
        List<Order> confirmedOrders = new LinkedList<>();

        orders.forEach(order -> {
            if (order.process(subscriberId, isSporadic, paymentService)) {
                confirmedOrders.add(order);
            }
        });

        return confirmedOrders;
    }

    public boolean hasOrder(MealKitUniqueIdentifier orderId) {
        return orders.stream().anyMatch(order -> order.getOrderId().equals(orderId));
    }

    public void updateToInDelivery(MealKitUniqueIdentifier orderId) {
        Order order = get(orderId);

        order.changeStatus(new InDeliveryStatus(order));
    }

    public void updateToInPreparation(MealKitUniqueIdentifier orderId) {
        Order order = get(orderId);

        order.changeStatus(new InPreparationStatus(order));
    }

    public void updateToReadyForPickUp(MealKitUniqueIdentifier orderId) {
        Order order = get(orderId);

        order.changeStatus(new ReadyForPickUpStatus(order));
    }

    public void updateToPickedUp(MealKitUniqueIdentifier orderId) {
        Order order = get(orderId);

        order.changeStatus(new PickedUpStatus(order));
    }

    private Order get(MealKitUniqueIdentifier orderId) {
        return orders.stream().filter(order -> order.getOrderId().equals(orderId)).findFirst().orElseThrow(OrderNotFoundException::new);
    }
}
