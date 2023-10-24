package ca.ulaval.glo4003.repul.small.shipping.domain.catalog;

import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.repul.fixture.shipping.LocationsCatalogFixture;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.LocationsCatalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationsCatalogTest {
    private static final String A_KITCHEN_LOCATION_NAME = "Desjardins";
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("id");
    private static final KitchenLocation A_KITCHEN_LOCATION = new KitchenLocation(A_KITCHEN_LOCATION_ID, A_KITCHEN_LOCATION_NAME);
    private static final String A_DELIVERY_LOCATION_NAME = "Desjardins";
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("id");
    private static final int A_TOTAL_CAPACITY = 3;
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_DELIVERY_LOCATION_NAME, A_TOTAL_CAPACITY);
    private static final DeliveryLocationId A_INVALID_DELIVERY_LOCATION_ID = new DeliveryLocationId("id invalid");
    private static final KitchenLocationId A_INVALID_KITCHEN_LOCATION_ID = new KitchenLocationId("id invalid");

    @Test
    public void givenValidKitchenLocationId_whenGetKitchenLocation_shouldReturnRightKitchenLocation() {
        LocationsCatalog locationsCatalog = new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).build();

        KitchenLocation kitchenLocation = locationsCatalog.getKitchenLocation(A_KITCHEN_LOCATION_ID);

        assertEquals(kitchenLocation, A_KITCHEN_LOCATION);
    }

    @Test
    public void givenValidDeliveryLocationId_whenGetDeliveryLocation_shouldReturnRightDeliveryLocation() {
        LocationsCatalog locationsCatalog = new LocationsCatalogFixture().addDeliveryLocation(A_DELIVERY_LOCATION).build();

        DeliveryLocation deliveryLocation = locationsCatalog.getDeliveryLocation(A_DELIVERY_LOCATION_ID);

        assertEquals(deliveryLocation, A_DELIVERY_LOCATION);
    }

    @Test
    public void givenInvalidDeliveryLocationId_whenGetDeliveryLocation_shouldThrowInvalidLocationException() {
        LocationsCatalog locationsCatalog = new LocationsCatalogFixture().build();

        assertThrows(InvalidLocationException.class, () -> locationsCatalog.getDeliveryLocation(A_INVALID_DELIVERY_LOCATION_ID));
    }

    @Test
    public void givenInvalidKitchenLocationId_whenGetKitchenLocation_shouldThrowInvalidLocationException() {
        LocationsCatalog locationsCatalog = new LocationsCatalogFixture().build();

        assertThrows(InvalidLocationException.class, () -> locationsCatalog.getKitchenLocation(A_INVALID_KITCHEN_LOCATION_ID));
    }

    @Test
    public void whenGetDeliveryLocations_shouldReturnDeliveryLocations() {
        LocationsCatalog locationsCatalog = new LocationsCatalogFixture().addDeliveryLocation(A_DELIVERY_LOCATION).build();

        List<DeliveryLocation> deliveryLocations = locationsCatalog.getDeliveryLocations();

        assertEquals(deliveryLocations, List.of(A_DELIVERY_LOCATION));
    }
}