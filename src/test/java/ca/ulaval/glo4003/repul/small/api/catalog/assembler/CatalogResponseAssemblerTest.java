package ca.ulaval.glo4003.repul.small.api.catalog.assembler;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.api.catalog.assembler.CatalogResponseAssembler;
import ca.ulaval.glo4003.repul.api.catalog.response.LocationResponse;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatalogResponseAssemblerTest {
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(new LocationId("VACHON"), "vch-1", 10);
    private static final PickupLocation A_SECOND_PICKUP_LOCATION = new PickupLocation(new LocationId("PEPS"), "pep-2", 20);
    private static final PickupLocation A_THIRD_PICKUP_LOCATION = new PickupLocation(new LocationId("DESJARDINS"), "dej-3", 30);
    private static final LocationsPayload A_LOCATIONS_PAYLOAD = new LocationsPayload(
        List.of(
            A_PICKUP_LOCATION, A_SECOND_PICKUP_LOCATION, A_THIRD_PICKUP_LOCATION
        )
    );

    private CatalogResponseAssembler catalogResponseAssembler;

    @BeforeEach
    public void createAssembler() {
        catalogResponseAssembler = new CatalogResponseAssembler();
    }

    @Test
    public void givenLocationsPayload_whenToLocationsResponse_shouldReturnListOfLocationResponse() {
        List<LocationResponse> locationResponses = catalogResponseAssembler.toLocationsResponse(A_LOCATIONS_PAYLOAD);

        assertEquals(A_PICKUP_LOCATION.getLocationId().value(), locationResponses.get(0).locationId());
        assertEquals(A_PICKUP_LOCATION.getName(), locationResponses.get(0).name());
        assertEquals(A_PICKUP_LOCATION.getRemainingCapacity(), locationResponses.get(0).remainingCapacity());
        assertEquals(A_SECOND_PICKUP_LOCATION.getLocationId().value(), locationResponses.get(1).locationId());
    }
}
