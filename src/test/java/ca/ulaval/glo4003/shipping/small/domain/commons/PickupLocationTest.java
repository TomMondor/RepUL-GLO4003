package ca.ulaval.glo4003.shipping.small.domain.commons;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.shipping.domain.commons.PickupLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PickupLocationTest {
    private static final LocationId A_VALID_LOCATION_ID = new LocationId("VACHON");
    private static final String A_VALID_LOCATION_NAME = "Vachon";

    @Test
    public void whenGetLocationId_shouldReturnLocationId() {
        PickupLocation pickupLocation = new PickupLocation(A_VALID_LOCATION_ID, A_VALID_LOCATION_NAME);

        assertEquals(A_VALID_LOCATION_ID, pickupLocation.getLocationId());
    }
}
