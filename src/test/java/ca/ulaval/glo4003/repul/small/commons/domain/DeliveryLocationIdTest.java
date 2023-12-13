package ca.ulaval.glo4003.repul.small.commons.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeliveryLocationIdTest {
    private static final String A_VALID_DELIVERY_LOCATION_ID = "VACHON";
    private static final String AN_INVALID_DELIVERY_LOCATION_ID = "INVALID";

    @Test
    public void givenValidDeliveryLocationId_whenFrom_shouldReturnDeliveryLocationId() {
        DeliveryLocationId deliveryLocationId = DeliveryLocationId.from(A_VALID_DELIVERY_LOCATION_ID);

        assertEquals(DeliveryLocationId.VACHON, deliveryLocationId);
    }

    @Test
    public void givenInvalidDeliveryLocationId_whenFrom_shouldThrowInvalidLocationIdException() {
        assertThrows(InvalidLocationIdException.class, () -> DeliveryLocationId.from(AN_INVALID_DELIVERY_LOCATION_ID));
    }
}
