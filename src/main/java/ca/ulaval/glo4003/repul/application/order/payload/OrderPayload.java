package ca.ulaval.glo4003.repul.application.order.payload;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;

public record OrderPayload(Lunchbox lunchbox, LocalDate deliveryDate, OrderStatus orderStatus) {
    public static OrderPayload from(Order order) {
        return new OrderPayload(order.getLunchbox(), order.getDeliveryDate(), order.getOrderStatus());
    }
}
