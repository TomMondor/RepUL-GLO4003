package ca.ulaval.glo4003.repul.small.api.catalog;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.api.catalog.CatalogResource;
import ca.ulaval.glo4003.repul.application.catalog.CatalogService;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationPayload;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CatalogResourceTest {
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(new LocationId("VACHON"), "toto", 10);
    private static final LocationPayload A_LOCATION_PAYLOAD = new LocationPayload(
        A_PICKUP_LOCATION.getLocationId(), A_PICKUP_LOCATION.getName(), A_PICKUP_LOCATION.getTotalCapacity(),
        A_PICKUP_LOCATION.getRemainingCapacity());
    private static final LocationsPayload A_LOCATIONS_PAYLOAD = new LocationsPayload(List.of(A_LOCATION_PAYLOAD));
    private CatalogResource catalogResource;
    private CatalogService catalogService;

    @BeforeEach
    public void createCatalogResource() {
        catalogService = mock(CatalogService.class);
        catalogResource = new CatalogResource(catalogService);
    }

    @Test
    public void whenGettingLocations_shouldReturn200() {
        when(catalogService.getPickupLocations()).thenReturn(A_LOCATIONS_PAYLOAD);

        Response response = catalogResource.getLocations();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingLocations_shouldGetLocations() {
        when(catalogService.getPickupLocations()).thenReturn(A_LOCATIONS_PAYLOAD);

        catalogResource.getLocations();

        verify(catalogService).getPickupLocations();
    }
}
