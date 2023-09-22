package ca.ulaval.glo4003.repul.small.domain.catalog;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationException;

import static org.junit.jupiter.api.Assertions.*;

class CatalogTest {
    private static final String A_LOCATION_ID = "a location id";
    private static final String A_MISSING_LOCATION_ID = "a missing location id";
    private static final String A_NAME = "a name";
    private static final int A_TOTAL_CAPACITY = 10;
    private static final String ANOTHER_LOCATION_ID = "another location id";
    private static final String ANOTHER_NAME = "another name";
    private static final int ANOTHER_TOTAL_CAPACITY = 20;
    private static final String A_SEMESTER_CODE = "E24";
    private static final Date A_START_DATE = new Date(1672531200000L);
    private static final Date AN_END_DATE = new Date(1682899200000L);

    private Catalog catalog;

    @BeforeEach
    void setUpCatalog() {
        List<PickupLocation> pickupLocations = List.of(new PickupLocation(new LocationId(A_LOCATION_ID), A_NAME, A_TOTAL_CAPACITY),
            new PickupLocation(new LocationId(ANOTHER_LOCATION_ID), ANOTHER_NAME, ANOTHER_TOTAL_CAPACITY));
        List<Semester> semesters = List.of(new Semester(new SemesterCode(A_SEMESTER_CODE), A_START_DATE, AN_END_DATE));
        catalog = new Catalog(pickupLocations, semesters);
    }

    @Test
    void givenExistingLocationId_whenValidateLocationId_shouldNotThrow() {
        assertDoesNotThrow(() -> catalog.validateLocationId(new LocationId(A_LOCATION_ID)));
    }

    @Test
    void givenMissingLocationId_whenValidateLocationId_shouldThrowInvalidLocationException() {
        assertThrows(InvalidLocationException.class, () -> catalog.validateLocationId(new LocationId(A_MISSING_LOCATION_ID)));
    }
}

