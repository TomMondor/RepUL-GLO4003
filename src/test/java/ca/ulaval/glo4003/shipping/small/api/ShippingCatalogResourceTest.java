package ca.ulaval.glo4003.shipping.small.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.shipping.api.ShippingCatalogResource;
import ca.ulaval.glo4003.shipping.application.ShippingCatalogService;
import ca.ulaval.glo4003.shipping.application.payload.LocationPayload;
import ca.ulaval.glo4003.shipping.application.payload.LocationsPayload;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShippingCatalogResourceTest {
    private static final ShippingLocation A_PICKUP_LOCATION = new ShippingLocation(new LocationId("VACHON"), "toto", 10);
    private static final LocationPayload A_LOCATION_PAYLOAD = new LocationPayload(
        A_PICKUP_LOCATION.getLocationId(), A_PICKUP_LOCATION.getName(), A_PICKUP_LOCATION.getTotalCapacity(),
        A_PICKUP_LOCATION.getRemainingCapacity());
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
