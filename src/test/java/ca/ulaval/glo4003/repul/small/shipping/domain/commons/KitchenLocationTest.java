package ca.ulaval.glo4003.repul.small.shipping.domain.commons;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KitchenLocationTest {
    private static final KitchenLocationId A_VALID_KITCHEN_LOCATION_ID = new KitchenLocationId("VACHON");
    private static final String A_VALID_KITCHEN_LOCATION_NAME = "Vachon";

    @Test
    public void whenGetLocationId_shouldReturnLocationId() {
        KitchenLocation kitchenLocation = new KitchenLocation(A_VALID_KITCHEN_LOCATION_ID, A_VALID_KITCHEN_LOCATION_NAME);

        assertEquals(A_VALID_KITCHEN_LOCATION_ID, kitchenLocation.getLocationId());
    }
}
