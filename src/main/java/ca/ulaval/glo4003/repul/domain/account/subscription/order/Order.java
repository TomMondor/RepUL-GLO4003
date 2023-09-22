package ca.ulaval.glo4003.repul.domain.account.subscription.order;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;

public class Order {
    private Lunchbox lunchbox;
    private OrderId orderId;
    private OrderStatus orderStatus;

    public Order(Lunchbox lunchbox, OrderId orderId, OrderStatus orderStatus) {
        this.lunchbox = lunchbox;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public Lunchbox getLunchbox() {
        return lunchbox;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
