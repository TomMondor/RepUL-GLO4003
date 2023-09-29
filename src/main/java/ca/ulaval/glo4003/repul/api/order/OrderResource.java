package ca.ulaval.glo4003.repul.api.order;

import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Role;
import ca.ulaval.glo4003.identitymanagement.middleware.Roles;
import ca.ulaval.glo4003.identitymanagement.middleware.Secure;
import ca.ulaval.glo4003.repul.api.order.assembler.OrdersResponseAssembler;
import ca.ulaval.glo4003.repul.api.order.response.OrderResponse;
import ca.ulaval.glo4003.repul.application.order.OrderService;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private final OrderService orderService;
    private final OrdersResponseAssembler ordersResponseAssembler = new OrdersResponseAssembler();

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GET
    @Secure
    @Roles(Role.CLIENT)
    @Path("/me")
    public Response getMyCurrentOrders(@Context ContainerRequestContext requestContext) {
        UniqueIdentifier accountId = (UniqueIdentifier) requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        OrdersPayload ordersPayload = orderService.getAccountCurrentOrders(accountId);

        List<OrderResponse> ordersResponse = ordersResponseAssembler.toOrdersResponse(ordersPayload);

        return Response.ok(ordersResponse).build();
    }
}
