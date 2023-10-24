package ca.ulaval.glo4003.repul.small.commons.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeliveryLocationIdTest {
    private static final String A_VALID_LOCATION_ID = "VACHON";

    @Test
    public void givenNullValue_whenCreatingLocationId_shouldThrowInvalidLocationIdException() {
        assertThrows(InvalidLocationIdException.class, () -> new DeliveryLocationId(null));
    }

    @Test
    public void givenBlankValue_whenCreatingLocationId_shouldThrowInvalidLocationIdException() {
        assertThrows(InvalidLocationIdException.class, () -> new DeliveryLocationId(""));
    }

    @Test
    public void givenValidValue_whenCreatingLocationId_shouldNotThrow() {
        assertDoesNotThrow(() -> new DeliveryLocationId(A_VALID_LOCATION_ID));
    }
}
