package ca.ulaval.glo4003.repul.domain.account.subscription.order;

import java.time.LocalDate;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.exception.OrderNotPendingException;

public class Order {
    private static final int DAYS_TO_CONFIRM = 2;

    private UniqueIdentifier orderId;
    private Lunchbox lunchbox;
    private OrderStatus orderStatus;
    private final LocalDate deliveryDate;

    public Order(UniqueIdentifier orderId, Lunchbox lunchbox, LocalDate deliveryDate, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.lunchbox = lunchbox;
        this.orderStatus = orderStatus;
        this.deliveryDate = deliveryDate;
    }

    public Order(UniqueIdentifier orderId, Lunchbox lunchbox, LocalDate deliveryDate) {
        this(orderId, lunchbox, deliveryDate, OrderStatus.PENDING);
    }

    public Lunchbox getLunchbox() {
        return lunchbox;
    }

    public UniqueIdentifier getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        updateStatusBasedOnDeliveryDate();
        return orderStatus;
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

    public boolean isToCookToday() {
        return deliveryDate.minusDays(1).equals(LocalDate.now());
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
}
