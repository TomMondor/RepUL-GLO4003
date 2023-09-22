package ca.ulaval.glo4003.repul.small.domain.catalog;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationIdException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationIdTest {
    private static final String A_VALID_LOCATION_ID = "VACHON";

    @Test
    public void givenNullValue_whenCreatingLocationId_shouldThrowInvalidLocationIdException() {
        assertThrows(InvalidLocationIdException.class, () -> new LocationId(null));
    }

    @Test
    public void givenBlankValue_whenCreatingLocationId_shouldThrowInvalidLocationIdException() {
        assertThrows(InvalidLocationIdException.class, () -> new LocationId(""));
    }

    @Test
    public void givenValidValue_whenCreatingLocationId_shouldNotThrow() {
        assertDoesNotThrow(() -> new LocationId(A_VALID_LOCATION_ID));
    }
}
