package ca.ulaval.glo4003.repul.small.application.order.payload;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.application.order.payload.OrderPayload;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;
import ca.ulaval.glo4003.repul.fixture.OrderFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersPayloadTest {
    private static final Lunchbox ORDER_LUNCHBOX = new LunchboxFixture().build();
    private static final LocalDate ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;

    @Test
    public void whenUsingFrom_shouldReturnCorrectOrdersPayload() {
        OrderPayload orderPayload = new OrderPayload(ORDER_LUNCHBOX, ORDER_DELIVERY_DATE, ORDER_STATUS);
        OrdersPayload expectedOrdersPayload = new OrdersPayload(List.of(orderPayload));
        Order order = new OrderFixture().withLunchbox(ORDER_LUNCHBOX).withDeliveryDate(ORDER_DELIVERY_DATE).withOrderStatus(ORDER_STATUS).build();

        OrdersPayload actualOrdersPayload = OrdersPayload.from(List.of(order));

        assertEquals(expectedOrdersPayload, actualOrdersPayload);
    }
}
