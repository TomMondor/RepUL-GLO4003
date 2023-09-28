package ca.ulaval.glo4003.repul.application.order.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;

public record OrdersPayload(List<Order> orders) {
}
