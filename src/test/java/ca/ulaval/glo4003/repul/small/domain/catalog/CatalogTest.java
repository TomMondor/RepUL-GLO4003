package ca.ulaval.glo4003.repul.small.domain.catalog;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogTest {
    private static final String A_LOCATION_ID = "a location id";
    private static final String A_NAME = "a name";
    private static final int A_TOTAL_CAPACITY = 10;
    private static final String ANOTHER_LOCATION_ID = "another location id";
    private static final String ANOTHER_NAME = "another name";
    private static final int ANOTHER_TOTAL_CAPACITY = 20;

    private Catalog catalog;

    @BeforeEach
    void setUpCatalog() {
        List<PickupLocation> pickupLocations = List.of(new PickupLocation(new LocationId(A_LOCATION_ID), A_NAME, A_TOTAL_CAPACITY),
            new PickupLocation(new LocationId(ANOTHER_LOCATION_ID), ANOTHER_NAME, ANOTHER_TOTAL_CAPACITY));
        catalog = new Catalog(pickupLocations);
    }

    @Test
    void givenExistingLocationId_whenGetPickupLocation_shouldReturnPickupLocation() {
        PickupLocation pickupLocation = catalog.getPickupLocation(new LocationId(A_LOCATION_ID));

        assertEquals(new LocationId(A_LOCATION_ID), pickupLocation.getLocationId());
    }

    @Test
    void givenMissingLocationId_whenGetPickupLocation_shouldThrowInvalidLocationException() {
        assertThrows(InvalidLocationException.class, () -> catalog.getPickupLocation(new LocationId("missing location id")));
    }
}

