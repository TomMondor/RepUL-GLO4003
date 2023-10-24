package ca.ulaval.glo4003.repul.subscription.domain.order;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderNotPendingException;

public class Order {
    private static final int DAYS_TO_CONFIRM = 2;
    private final LocalDate deliveryDate;
    private final UniqueIdentifier orderId;
    private final MealKitType mealKitType;
    private OrderStatus orderStatus;

    public Order(UniqueIdentifier orderId, MealKitType mealKitType, LocalDate deliveryDate, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.mealKitType = mealKitType;
        this.orderStatus = orderStatus;
        this.deliveryDate = deliveryDate;
    }

    public Order(UniqueIdentifier orderId, MealKitType mealKitType, LocalDate deliveryDate) {
        this(orderId, mealKitType, deliveryDate, OrderStatus.PENDING);
    }

    public MealKitType getMealKitType() {
        return mealKitType;
    }

    public OrderStatus getOrderStatus() {
        updateStatusBasedOnDeliveryDate();
        return orderStatus;
    }

    public UniqueIdentifier getOrderId() {
        return orderId;
    }

    public boolean isInTheFuture() {
        LocalDate today = LocalDate.now();
        return deliveryDate.isAfter(today) || deliveryDate.isEqual(today);
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void confirm() {
        verifyIfOrderIsPending();
        if (LocalDate.now().isBefore(this.deliveryDate.minusDays(DAYS_TO_CONFIRM))) {
            orderStatus = OrderStatus.TO_COOK;
        } else {
            orderStatus = OrderStatus.DECLINED;
            throw new OrderNotPendingException();
        }
    }

    public void decline() {
        verifyIfOrderIsPending();
        orderStatus = OrderStatus.DECLINED;
    }

    private void verifyIfOrderIsPending() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderNotPendingException();
        }
    }

    private void updateStatusBasedOnDeliveryDate() {
        if (orderStatus == OrderStatus.PENDING && this.deliveryDate.minusDays(DAYS_TO_CONFIRM).isBefore(LocalDate.now())) {
            orderStatus = OrderStatus.DECLINED;
        }
    }

    public void markAsCooked() {
        orderStatus = OrderStatus.TO_DELIVER;
    }

    public void markAsInDelivery() {
        orderStatus = OrderStatus.IN_DELIVERY;
    }
}