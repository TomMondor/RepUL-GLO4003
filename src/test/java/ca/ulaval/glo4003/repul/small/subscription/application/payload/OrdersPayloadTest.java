package ca.ulaval.glo4003.repul.small.subscription.application.payload;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersPayloadTest {
    private static final MealKitType MEALKIT_TYPE = MealKitType.STANDARD;
    private static final LocalDate ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;
    private static final MealKitUniqueIdentifier ORDER_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();

    @Test
    public void whenUsingFrom_shouldReturnCorrectOrdersPayload() {
        OrderPayload orderPayload =
            new OrderPayload(ORDER_ID.getUUID().toString(), MEALKIT_TYPE.toString(), ORDER_DELIVERY_DATE.toString(), ORDER_STATUS.toString());
        OrdersPayload expectedOrdersPayload = new OrdersPayload(List.of(orderPayload));
        Order order =
            new OrderFixture().withOrderId(ORDER_ID).withMealKitType(MEALKIT_TYPE).withDeliveryDate(ORDER_DELIVERY_DATE).withOrderStatus(ORDER_STATUS).build();

        OrdersPayload actualOrdersPayload = OrdersPayload.from(List.of(order));

        assertEquals(expectedOrdersPayload, actualOrdersPayload);
    }
}
