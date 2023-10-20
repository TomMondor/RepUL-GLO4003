package ca.ulaval.glo4003.shipping.small.api.assembler;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.shipping.api.assembler.ShippingCatalogResponseAssembler;
import ca.ulaval.glo4003.shipping.api.response.LocationResponse;
import ca.ulaval.glo4003.shipping.application.payload.LocationPayload;
import ca.ulaval.glo4003.shipping.application.payload.LocationsPayload;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCatalogResponseAssemblerTest {
    private static final ShippingLocation A_SHIPPING_LOCATION = new ShippingLocation(new LocationId("VACHON"), "vch-1", 10);
    private static final ShippingLocation A_SECOND_SHIPPING_LOCATION = new ShippingLocation(new LocationId("PEPS"), "pep-2", 20);
    private static final ShippingLocation A_THIRD_SHIPPING_LOCATION = new ShippingLocation(new LocationId("DESJARDINS"), "dej-3", 30);
    private static final LocationPayload A_LOCATION_PAYLOAD = new LocationPayload(
        A_SHIPPING_LOCATION.getLocationId(), A_SHIPPING_LOCATION.getName(), A_SHIPPING_LOCATION.getTotalCapacity(),
        A_SHIPPING_LOCATION.getRemainingCapacity()
    );
    private static final LocationPayload A_SECOND_LOCATION_PAYLOAD = new LocationPayload(
        A_SECOND_SHIPPING_LOCATION.getLocationId(), A_SECOND_SHIPPING_LOCATION.getName(), A_SECOND_SHIPPING_LOCATION.getTotalCapacity(),
        A_SECOND_SHIPPING_LOCATION.getRemainingCapacity()
    );
    private static final LocationPayload A_THIRD_LOCATION_PAYLOAD = new LocationPayload(
        A_THIRD_SHIPPING_LOCATION.getLocationId(), A_THIRD_SHIPPING_LOCATION.getName(), A_THIRD_SHIPPING_LOCATION.getTotalCapacity(),
        A_THIRD_SHIPPING_LOCATION.getRemainingCapacity()
    );
    private static final LocationsPayload A_LOCATIONS_PAYLOAD = new LocationsPayload(
        List.of(
            A_LOCATION_PAYLOAD, A_SECOND_LOCATION_PAYLOAD, A_THIRD_LOCATION_PAYLOAD
        )
    );

    private ShippingCatalogResponseAssembler catalogResponseAssembler;

    @BeforeEach
    public void createAssembler() {
        catalogResponseAssembler = new ShippingCatalogResponseAssembler();
    }

    @Test
    public void givenLocationsPayload_whenToLocationsResponse_shouldReturnListOfLocationResponse() {
        List<LocationResponse> locationResponses = catalogResponseAssembler.toLocationsResponse(A_LOCATIONS_PAYLOAD);

        assertEquals(A_SHIPPING_LOCATION.getLocationId().value(), locationResponses.get(0).locationId());
        assertEquals(A_SHIPPING_LOCATION.getName(), locationResponses.get(0).name());
        assertEquals(A_SHIPPING_LOCATION.getRemainingCapacity(), locationResponses.get(0).remainingCapacity());
        assertEquals(A_SECOND_SHIPPING_LOCATION.getLocationId().value(), locationResponses.get(1).locationId());
    }
}
