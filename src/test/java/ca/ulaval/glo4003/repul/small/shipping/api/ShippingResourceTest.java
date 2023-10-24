package ca.ulaval.glo4003.repul.small.shipping.api;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.api.ShippingResource;
import ca.ulaval.glo4003.repul.shipping.application.ShippingService;
import ca.ulaval.glo4003.repul.shipping.application.payload.MealKitShippingStatusPayload;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingStatus;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShippingResourceTest {
    private static final String A_SHIPPING_ID = UUID.randomUUID().toString();
    private static final String A_SHIPPER_ID = UUID.randomUUID().toString();
    private static final String A_TICKET_ID = UUID.randomUUID().toString();
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_SHIPPER_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPER_ID);
    private static final UniqueIdentifier A_SHIPPING_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPING_ID);
    private static final UniqueIdentifier A_TICKET_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_TICKET_ID);
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_MEAL_KIT_ID);
    private static final MealKitShippingStatusPayload A_MEAL_KIT_SHIPPING_STATUS_PAYLOAD =
        new MealKitShippingStatusPayload(A_TICKET_ID, A_MEAL_KIT_ID, ShippingStatus.IN_DELIVERY.toString());
    private ShippingResource shippingResource;
    private ShippingService shippingService;
    @Mock
    private ContainerRequestContext requestContext;

    @BeforeEach
    public void createShippingResource() {
        shippingService = mock(ShippingService.class);
        shippingResource = new ShippingResource(shippingService);
    }

    @Test
    public void whenCancellingShipping_shouldReturn200() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        Response response = shippingResource.cancelShipping(requestContext, A_SHIPPING_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenCancellingShipping_shouldCancelShipping() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        shippingResource.cancelShipping(requestContext, A_SHIPPING_ID);

        verify(shippingService).cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenPickupCargo_shouldReturn200() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        Response response = shippingResource.pickupCargo(requestContext, A_SHIPPING_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenPickupCargo_shouldPickupCargo() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        shippingResource.pickupCargo(requestContext, A_SHIPPING_ID);

        verify(shippingService).pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenConfirmingShipping_shouldReturn204() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        Response response = shippingResource.confirmShipping(requestContext, A_TICKET_ID, A_SHIPPING_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenConfirmingShipping_shouldConfirmShipping() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        shippingResource.confirmShipping(requestContext, A_TICKET_ID, A_SHIPPING_ID);

        verify(shippingService).confirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenUnconfirmingShipping_shouldReturn200() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        Response response = shippingResource.unconfirmShipping(requestContext, A_TICKET_ID, A_SHIPPING_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenUnconfirmingShipping_shouldUnconfirmShipping() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);

        shippingResource.unconfirmShipping(requestContext, A_TICKET_ID, A_SHIPPING_ID);

        verify(shippingService).unconfirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenGettingMealKitShippingStatus_shouldReturn200() {
        when(shippingService.getShippingStatus(A_MEAL_KIT_UNIQUE_IDENTIFIER)).thenReturn(A_MEAL_KIT_SHIPPING_STATUS_PAYLOAD);

        Response response = shippingResource.getMealKitShippingStatus(A_MEAL_KIT_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingMealKitShippingStatus_shouldGetMealKitShippingStatus() {
        when(shippingService.getShippingStatus(A_MEAL_KIT_UNIQUE_IDENTIFIER)).thenReturn(A_MEAL_KIT_SHIPPING_STATUS_PAYLOAD);

        shippingResource.getMealKitShippingStatus(A_MEAL_KIT_ID);

        verify(shippingService).getShippingStatus(A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }
}
