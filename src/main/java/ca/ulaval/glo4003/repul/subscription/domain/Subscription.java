package ca.ulaval.glo4003.repul.subscription.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoNextOrderInSubscriptionException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public class Subscription {
    private final UniqueIdentifier subscriberId;
    private final UniqueIdentifier subscriptionId;
    private final List<Order> orders;
    private final Frequency frequency;
    private final DeliveryLocationId deliveryLocationId;
    private final LocalDate startDate;
    private final Semester semester;
    private final MealKitType mealKitType;

    public Subscription(UniqueIdentifier subscriptionId, UniqueIdentifier subscriberId, List<Order> orders, Frequency frequency,
                        DeliveryLocationId deliveryLocationId,
                        LocalDate startDate, Semester semester, MealKitType mealKitType) {
        this.subscriptionId = subscriptionId;
        this.subscriberId = subscriberId;
        this.orders = orders;
        this.frequency = frequency;
        this.deliveryLocationId = deliveryLocationId;
        this.startDate = startDate;
        this.semester = semester;
        this.mealKitType = mealKitType;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public UniqueIdentifier getSubscriptionId() {
        return subscriptionId;
    }

    public UniqueIdentifier getSubscriberId() {
        return subscriberId;
    }

    public DeliveryLocationId getDeliveryLocationId() {
        return deliveryLocationId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Semester getSemester() {
        return semester;
    }

    public MealKitType getMealKitType() {
        return mealKitType;
    }

    public Order confirmNextMealKit() {
        Order order = this.findNextOrder();
        order.confirm();
        return order;
    }

    public Order declineNextMealKit() {
        Order order = this.findNextOrder();
        order.decline();
        return order;
    }

    private Order findNextOrder() {
        return orders.stream().filter(Order::isInTheFuture).findFirst().orElseThrow(NoNextOrderInSubscriptionException::new);
    }

    public Optional<Order> findCurrentOrder() {
        return orders.stream().filter(Order::isInTheFuture).findFirst();
    }

    public void markAsCooked() {
        this.findCurrentOrder().ifPresent(Order::markAsCooked);
    }

    public void markAsInDelivery() {
        this.findCurrentOrder().ifPresent(Order::markAsInDelivery);
    }
}
