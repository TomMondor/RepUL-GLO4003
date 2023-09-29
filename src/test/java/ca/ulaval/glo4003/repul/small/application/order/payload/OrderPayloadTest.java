package ca.ulaval.glo4003.repul.small.application.order.payload;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.application.order.payload.OrderPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;
import ca.ulaval.glo4003.repul.fixture.OrderFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderPayloadTest {
    private static final Lunchbox ORDER_LUNCHBOX = new LunchboxFixture().build();
    private static final LocalDate ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;

    @Test
    public void whenUsingFrom_shouldReturnCorrectOrderPayload() {
        OrderPayload expectedOrderPayload = new OrderPayload(ORDER_LUNCHBOX, ORDER_DELIVERY_DATE, ORDER_STATUS);
        Order order = new OrderFixture().withLunchbox(ORDER_LUNCHBOX).withDeliveryDate(ORDER_DELIVERY_DATE).withOrderStatus(ORDER_STATUS).build();

        OrderPayload actualOrderPayload = OrderPayload.from(order);

        assertEquals(expectedOrderPayload, actualOrderPayload);
    }
}
