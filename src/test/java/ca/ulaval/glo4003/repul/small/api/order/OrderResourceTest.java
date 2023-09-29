package ca.ulaval.glo4003.repul.small.api.order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.api.order.OrderResource;
import ca.ulaval.glo4003.repul.application.order.OrderService;
import ca.ulaval.glo4003.repul.application.order.payload.OrderPayload;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderResourceTest {
    private static final Lunchbox AN_ORDER_LUNCHBOX = new LunchboxFixture().build();
    private static final LocalDate AN_ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus AN_ORDER_STATUS = OrderStatus.PENDING;
    private static final OrdersPayload AN_ORDERS_PAYLOAD =
        new OrdersPayload(List.of(new OrderPayload(AN_ORDER_LUNCHBOX, AN_ORDER_DELIVERY_DATE, AN_ORDER_STATUS)));
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private static final UniqueIdentifier A_UNIQUE_IDENTIFIER = new UniqueIdentifier(UUID.randomUUID());
    private OrderResource orderResource;
    @Mock
    private OrderService orderService;
    @Mock
    private ContainerRequestContext requestContext;

    @BeforeEach
    public void createOrderResource() {
        orderResource = new OrderResource(orderService);
        given(requestContext.getProperty(any())).willReturn(A_UNIQUE_IDENTIFIER);
        given(orderService.getAccountCurrentOrders(any())).willReturn(AN_ORDERS_PAYLOAD);
    }

    @Test
    public void whenGettingMyOrders_shouldGetAccountId() {
        orderResource.getMyCurrentOrders(requestContext);

        verify(requestContext).getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
    }

    @Test
    public void whenGettingMyOrders_shouldGetOrders() {
        orderResource.getMyCurrentOrders(requestContext);

        verify(orderService).getAccountCurrentOrders(A_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenGettingMyOrders_shouldReturn200() {
        Response response = orderResource.getMyCurrentOrders(requestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
