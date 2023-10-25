package ca.ulaval.glo4003.repul.small.delivery.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.api.LocationsCatalogResource;
import ca.ulaval.glo4003.repul.delivery.application.LocationsCatalogService;
import ca.ulaval.glo4003.repul.delivery.application.payload.LocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LocationsCatalogResourceTest {
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(new DeliveryLocationId("VACHON"), "toto", 10);
    private static final LocationPayload A_LOCATION_PAYLOAD =
        new LocationPayload(A_DELIVERY_LOCATION.getLocationId(), A_DELIVERY_LOCATION.getName(), A_DELIVERY_LOCATION.getTotalCapacity(),
            A_DELIVERY_LOCATION.getRemainingCapacity());
    private static final LocationsPayload A_LOCATIONS_PAYLOAD = new LocationsPayload(List.of(A_LOCATION_PAYLOAD));
    private LocationsCatalogResource locationsCatalogResource;
    private LocationsCatalogService locationsCatalogService;

    @BeforeEach
    public void createLocationsCatalogResource() {
        locationsCatalogService = mock(LocationsCatalogService.class);
        locationsCatalogResource = new LocationsCatalogResource(locationsCatalogService);
    }

    @Test
    public void whenGettingLocations_shouldReturn200() {
        when(locationsCatalogService.getDeliveryLocations()).thenReturn(A_LOCATIONS_PAYLOAD);

        Response response = locationsCatalogResource.getLocations();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingLocations_shouldGetLocations() {
        when(locationsCatalogService.getDeliveryLocations()).thenReturn(A_LOCATIONS_PAYLOAD);

        locationsCatalogResource.getLocations();

        verify(locationsCatalogService).getDeliveryLocations();
    }
}
