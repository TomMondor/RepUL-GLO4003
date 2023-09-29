package ca.ulaval.glo4003.repul.small.application.catalog.payload;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.application.catalog.payload.LocationPayload;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationPayloadTest {
    private static final LocationId LOCATION_ID = new LocationId("a location id");
    private static final String LOCATION_NAME = "a name";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final int LOCATION_REMAINING_CAPACITY = LOCATION_TOTAL_CAPACITY;

    @Test
    public void givenLocation_whenUsingFrom_shouldReturnCorrectLocationPayload() {
        LocationPayload expectedLocationPayload = new LocationPayload(LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY, LOCATION_REMAINING_CAPACITY);
        PickupLocation location = new PickupLocation(LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);

        LocationPayload actualLocationPayload = LocationPayload.from(location);

        assertEquals(expectedLocationPayload, actualLocationPayload);
    }
}
