package ca.ulaval.glo4003.repul.small.api.order.assembler;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.api.order.assembler.OrdersResponseAssembler;
import ca.ulaval.glo4003.repul.api.order.response.OrderResponse;
import ca.ulaval.glo4003.repul.application.order.payload.OrderPayload;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;
import ca.ulaval.glo4003.repul.fixture.RecipeFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersResponseAssemblerTest {
    private static final UniqueIdentifier AN_ORDER_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final String ORDER_RECIPE_NAME = "EL SPAGHAT";
    private static final Lunchbox A_LUNCHBOX = new LunchboxFixture().withRecipes(List.of(new RecipeFixture().withName(ORDER_RECIPE_NAME).build())).build();
    private static final int DAYS_TO_CONFIRM = 2;
    private static final LocalDate ORDER_DELIVERY_DATE = LocalDate.now().plusDays(DAYS_TO_CONFIRM + 1);
    private static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;
    private static final OrdersPayload AN_ORDERS_PAYLOAD = new OrdersPayload(List.of(new OrderPayload(A_LUNCHBOX, ORDER_DELIVERY_DATE, ORDER_STATUS)));

    @Test
    public void givenValidOrdersPayload_whenToOrdersResponse_shouldReturnListOfOrderResponse() {
        OrdersResponseAssembler ordersResponseAssembler = new OrdersResponseAssembler();

        List<OrderResponse> orderResponses = ordersResponseAssembler.toOrdersResponse(AN_ORDERS_PAYLOAD);

        assertEquals(ORDER_DELIVERY_DATE.toString(), orderResponses.get(0).deliveryDate());
        assertEquals(ORDER_STATUS.toString(), orderResponses.get(0).orderStatus());
        assertEquals(List.of(ORDER_RECIPE_NAME), orderResponses.get(0).recipeNames());
    }
}
