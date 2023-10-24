package ca.ulaval.glo4003.repul.small.shipping.api.assembler;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.shipping.api.assembler.ShippingCatalogResponseAssembler;
import ca.ulaval.glo4003.repul.shipping.api.response.LocationResponse;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationPayload;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCatalogResponseAssemblerTest {
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(new DeliveryLocationId("VACHON"), "vch-1", 10);
    private static final DeliveryLocation A_SECOND_DELIVERY_LOCATION = new DeliveryLocation(new DeliveryLocationId("PEPS"), "pep-2", 20);
    private static final DeliveryLocation A_THIRD_DELIVERY_LOCATION = new DeliveryLocation(new DeliveryLocationId("DESJARDINS"), "dej-3", 30);
    private static final LocationPayload A_LOCATION_PAYLOAD =
        new LocationPayload(A_DELIVERY_LOCATION.getLocationId(), A_DELIVERY_LOCATION.getName(), A_DELIVERY_LOCATION.getTotalCapacity(),
            A_DELIVERY_LOCATION.getRemainingCapacity());
    private static final LocationPayload A_SECOND_LOCATION_PAYLOAD =
        new LocationPayload(A_SECOND_DELIVERY_LOCATION.getLocationId(), A_SECOND_DELIVERY_LOCATION.getName(), A_SECOND_DELIVERY_LOCATION.getTotalCapacity(),
            A_SECOND_DELIVERY_LOCATION.getRemainingCapacity());
    private static final LocationPayload A_THIRD_LOCATION_PAYLOAD =
        new LocationPayload(A_THIRD_DELIVERY_LOCATION.getLocationId(), A_THIRD_DELIVERY_LOCATION.getName(), A_THIRD_DELIVERY_LOCATION.getTotalCapacity(),
            A_THIRD_DELIVERY_LOCATION.getRemainingCapacity());
    private static final LocationsPayload A_LOCATIONS_PAYLOAD =
        new LocationsPayload(List.of(A_LOCATION_PAYLOAD, A_SECOND_LOCATION_PAYLOAD, A_THIRD_LOCATION_PAYLOAD));

    private ShippingCatalogResponseAssembler catalogResponseAssembler;

    @BeforeEach
    public void createAssembler() {
        catalogResponseAssembler = new ShippingCatalogResponseAssembler();
    }

    @Test
    public void givenLocationsPayload_whenToLocationsResponse_shouldReturnListOfLocationResponse() {
        List<LocationResponse> locationResponses = catalogResponseAssembler.toLocationsResponse(A_LOCATIONS_PAYLOAD);

        assertEquals(A_DELIVERY_LOCATION.getLocationId().value(), locationResponses.get(0).locationId());
        assertEquals(A_DELIVERY_LOCATION.getName(), locationResponses.get(0).name());
        assertEquals(A_DELIVERY_LOCATION.getRemainingCapacity(), locationResponses.get(0).remainingCapacity());
        assertEquals(A_SECOND_DELIVERY_LOCATION.getLocationId().value(), locationResponses.get(1).locationId());
    }
}
