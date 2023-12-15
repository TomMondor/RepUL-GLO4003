package ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status;

public enum OrderStatus {
    PENDING,
    DECLINED,
    CONFIRMED,
    IN_PREPARATION,
    IN_DELIVERY,
    READY_FOR_PICK_UP,
    PICKED_UP
}
