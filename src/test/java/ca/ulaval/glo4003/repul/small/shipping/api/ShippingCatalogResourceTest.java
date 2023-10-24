package ca.ulaval.glo4003.repul.small.shipping.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.shipping.api.ShippingCatalogResource;
import ca.ulaval.glo4003.repul.shipping.application.ShippingCatalogService;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationPayload;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ShippingCatalogResourceTest {
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(new DeliveryLocationId("VACHON"), "toto", 10);
    private static final LocationPayload A_LOCATION_PAYLOAD =
        new LocationPayload(A_DELIVERY_LOCATION.getLocationId(), A_DELIVERY_LOCATION.getName(), A_DELIVERY_LOCATION.getTotalCapacity(),
            A_DELIVERY_LOCATION.getRemainingCapacity());
    private static final LocationsPayload A_LOCATIONS_PAYLOAD = new LocationsPayload(List.of(A_LOCATION_PAYLOAD));
    private ShippingCatalogResource shippingCatalogResource;
    private ShippingCatalogService shippingCatalogService;

    @BeforeEach
    public void createShippingCatalogResource() {
        shippingCatalogService = mock(ShippingCatalogService.class);
        shippingCatalogResource = new ShippingCatalogResource(shippingCatalogService);
    }

    @Test
    public void whenGettingLocations_shouldReturn200() {
        when(shippingCatalogService.getShippingLocations()).thenReturn(A_LOCATIONS_PAYLOAD);

        Response response = shippingCatalogResource.getLocations();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingLocations_shouldGetLocations() {
        when(shippingCatalogService.getShippingLocations()).thenReturn(A_LOCATIONS_PAYLOAD);

        shippingCatalogResource.getLocations();

        verify(shippingCatalogService).getShippingLocations();
    }
}
