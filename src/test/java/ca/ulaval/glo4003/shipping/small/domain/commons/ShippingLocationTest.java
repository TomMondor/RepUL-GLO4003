package ca.ulaval.glo4003.shipping.small.domain.commons;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingLocationTest {
    private static final LocationId A_VALID_LOCATION_ID = new LocationId("VACHON");
    private static final String A_VALID_LOCATION_NAME = "Vachon";
    private static final int A_VALID_LOCATION_CAPACITY = 10;

    @Test
    public void givenValidValues_whenCreatingShippingLocation_shouldCreateShippingLocationWithRightValues() {
        ShippingLocation shippingLocation = new ShippingLocation(A_VALID_LOCATION_ID, A_VALID_LOCATION_NAME, A_VALID_LOCATION_CAPACITY);

        assertEquals(A_VALID_LOCATION_ID, shippingLocation.getLocationId());
        assertEquals(A_VALID_LOCATION_NAME, shippingLocation.getName());
        assertEquals(A_VALID_LOCATION_CAPACITY, shippingLocation.getTotalCapacity());
        assertEquals(A_VALID_LOCATION_CAPACITY, shippingLocation.getRemainingCapacity());
    }
}
