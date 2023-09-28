package ca.ulaval.glo4003.repul.api.order.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.api.order.response.OrderResponse;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;

public class OrdersResponseAssembler {
    public List<OrderResponse> toOrdersResponse(OrdersPayload ordersPayload) {
        return ordersPayload.orders().stream().map(order -> new OrderResponse(order.getDeliveryDate().toString(), order.getOrderStatus().toString(),
            order.getLunchbox().recipes().stream().map(Recipe::name).toList())).toList();
    }
}
