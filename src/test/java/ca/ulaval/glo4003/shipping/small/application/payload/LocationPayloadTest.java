package ca.ulaval.glo4003.shipping.small.application.payload;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.shipping.application.payload.LocationPayload;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationPayloadTest {
    private static final LocationId LOCATION_ID = new LocationId("a location id");
    private static final String LOCATION_NAME = "a name";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final int LOCATION_REMAINING_CAPACITY = LOCATION_TOTAL_CAPACITY;

    @Test
    public void givenLocation_whenUsingFrom_shouldReturnCorrectLocationPayload() {
        LocationPayload expectedLocationPayload = new LocationPayload(LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY, LOCATION_REMAINING_CAPACITY);
        ShippingLocation location = new ShippingLocation(LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);

        LocationPayload actualLocationPayload = LocationPayload.from(location);

        assertEquals(expectedLocationPayload, actualLocationPayload);
    }
}
