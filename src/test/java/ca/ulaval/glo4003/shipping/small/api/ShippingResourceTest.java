package ca.ulaval.glo4003.shipping.small.api;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.shipping.api.ShippingResource;
import ca.ulaval.glo4003.shipping.application.ShippingService;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShippingResourceTest {
    private static final String A_SHIPPING_ID = UUID.randomUUID().toString();
    private static final String A_SHIPPER_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_SHIPPER_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPER_ID);
    private static final UniqueIdentifier A_SHIPPING_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPING_ID);
    private ShippingResource shippingResource;
    private ShippingService shippingService;
    @Mock
    private ContainerRequestContext requestContext;

    @BeforeEach
    public void createShippingResource() {
        given(requestContext.getProperty(any())).willReturn(A_SHIPPER_UNIQUE_IDENTIFIER);
        shippingService = mock(ShippingService.class);
        shippingResource = new ShippingResource(shippingService);
    }

    @Test
    public void whenCancellingShipping_shouldReturn200() {
        Response response = shippingResource.cancelShipping(requestContext, A_SHIPPING_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenCancellingShipping_shouldCancelShipping() {
        shippingResource.cancelShipping(requestContext, A_SHIPPING_ID);

        verify(shippingService).cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenPickupCargo_shouldReturn200() {
        Response response = shippingResource.pickupCargo(requestContext, A_SHIPPING_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenPickupCargo_shouldPickupCargo() {
        shippingResource.pickupCargo(requestContext, A_SHIPPING_ID);

        verify(shippingService).pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }
}
