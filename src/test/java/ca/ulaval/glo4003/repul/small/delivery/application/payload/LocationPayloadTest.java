package ca.ulaval.glo4003.repul.small.delivery.application.payload;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.application.payload.LocationPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationPayloadTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("a location id");
    private static final String LOCATION_NAME = "a name";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final int LOCATION_REMAINING_CAPACITY = LOCATION_TOTAL_CAPACITY;

    @Test
    public void givenLocation_whenUsingFrom_shouldReturnCorrectLocationPayload() {
        LocationPayload expectedLocationPayload =
            new LocationPayload(A_DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY, LOCATION_REMAINING_CAPACITY);
        DeliveryLocation location = new DeliveryLocation(A_DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);

        LocationPayload actualLocationPayload = LocationPayload.from(location);

        assertEquals(expectedLocationPayload, actualLocationPayload);
    }
}
