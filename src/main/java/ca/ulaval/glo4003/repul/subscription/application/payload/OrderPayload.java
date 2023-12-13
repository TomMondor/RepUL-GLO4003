package ca.ulaval.glo4003.repul.subscription.application.payload;

import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public record OrderPayload(String orderId, String mealKitType, String deliveryDate, String orderStatus) {
    public static OrderPayload from(Order order) {
        return new OrderPayload(order.getOrderId().getUUID().toString(), order.getMealKitType().toString(), order.getDeliveryDate().toString(),
            order.getOrderStatus().toString());
    }
}
