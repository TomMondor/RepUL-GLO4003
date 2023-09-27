package ca.ulaval.glo4003.repul.domain.account.subscription.order;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.exception.OrderAlreadyStartedException;

public class Order {
    private static final int DAYS_TO_CONFIRM = 2;
    private final Lunchbox lunchbox;
    private final OrderId orderId;
    private final LocalDate deliveryDate;
    private OrderStatus orderStatus;

    public Order(Lunchbox lunchbox, OrderId orderId, OrderStatus orderStatus, LocalDate deliveryDate) {
        this.lunchbox = lunchbox;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.deliveryDate = deliveryDate;
    }

    public Lunchbox getLunchbox() {
        return lunchbox;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        updateStatusBasedOnDeliveryDate();
        return orderStatus;
    }

    public boolean isInTheFuture() {
        return deliveryDate.isAfter(LocalDate.now());
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void confirm() {
        verifyIfOrderIsStarted();
        if (LocalDate.now().isBefore(this.deliveryDate.minusDays(DAYS_TO_CONFIRM))) {
            orderStatus = OrderStatus.TO_PREPARE;
        } else {
            orderStatus = OrderStatus.DECLINED;
        }
    }

    public void decline() {
        verifyIfOrderIsStarted();
        orderStatus = OrderStatus.DECLINED;
    }

    private void verifyIfOrderIsStarted() {
        if (orderStatus != OrderStatus.DECLINED && orderStatus != OrderStatus.PENDING && orderStatus != OrderStatus.TO_PREPARE) {
            throw new OrderAlreadyStartedException();
        }
    }

    private void updateStatusBasedOnDeliveryDate() {
        if (orderStatus == OrderStatus.PENDING && this.deliveryDate.minusDays(DAYS_TO_CONFIRM).isBefore(LocalDate.now())) {
            orderStatus = OrderStatus.DECLINED;
        }
    }
}
