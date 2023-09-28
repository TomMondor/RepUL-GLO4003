package ca.ulaval.glo4003.repul.fixture;

import java.time.LocalDate;
import java.util.UUID;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;

public class OrderFixture {
    private UniqueIdentifier orderId;
    private Lunchbox lunchbox;
    private OrderStatus orderStatus;
    private LocalDate deliveryDate;

    public OrderFixture() {
        this.orderId = new UniqueIdentifier(UUID.randomUUID());
        this.lunchbox = new LunchboxFixture().build();
        this.orderStatus = OrderStatus.PENDING;
        this.deliveryDate = LocalDate.now().plusDays(4);
    }

    public OrderFixture withOrderId(UniqueIdentifier orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderFixture withLunchbox(Lunchbox lunchbox) {
        this.lunchbox = lunchbox;
        return this;
    }

    public OrderFixture withOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public OrderFixture withDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public OrderFixture withDeliveryDateIn(long days) {
        this.deliveryDate = LocalDate.now().plusDays(days);
        return this;
    }

    public Order build() {
        return new Order(orderId, lunchbox, deliveryDate, orderStatus);
    }
}
