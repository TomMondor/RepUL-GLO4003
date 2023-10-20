package ca.ulaval.glo4003.shipping.small.domain.catalog;

import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.commons.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.shipping.domain.catalog.ShippingCatalog;
import ca.ulaval.glo4003.shipping.domain.commons.PickupLocation;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;
import ca.ulaval.glo4003.shipping.fixture.ShippingCatalogFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShippingCatalogTest {
    private static final String A_PICKUP_LOCATION_NAME = "Desjardins";
    private static final LocationId A_PICKUP_LOCATION_ID = new LocationId("id");
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(A_PICKUP_LOCATION_ID, A_PICKUP_LOCATION_NAME);
    private static final String A_SHIPPING_LOCATION_NAME = "Desjardins";
    private static final LocationId A_SHIPPING_LOCATION_ID = new LocationId("id");
    private static final int A_TOTAL_CAPACITY = 3;
    private static final ShippingLocation A_SHIPPING_LOCATION = new ShippingLocation(A_SHIPPING_LOCATION_ID, A_SHIPPING_LOCATION_NAME, A_TOTAL_CAPACITY);
    private static final LocationId A_INVALID_SHIPPING_LOCATION_ID = new LocationId("id invalid");

    @Test
    public void givenValidLocationId_whenGetPickupLocation_shouldReturnRightPickupLocation() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().addPickupLocation(A_PICKUP_LOCATION).build();

        PickupLocation pickupLocation = shippingCatalog.getPickupLocation(A_PICKUP_LOCATION_ID);

        assertEquals(pickupLocation, A_PICKUP_LOCATION);
    }

    @Test
    public void givenValidLocationId_whenGetShippingLocation_shouldReturnRightShippingLocation() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().addShippingLocation(A_SHIPPING_LOCATION).build();

        ShippingLocation shippingLocation = shippingCatalog.getShippingLocation(A_SHIPPING_LOCATION_ID);

        assertEquals(shippingLocation, A_SHIPPING_LOCATION);
    }

    @Test
    public void givenInvalidLocationId_whenGetShippingLocation_shouldThrowInvalidLocationException() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().build();

        assertThrows(InvalidLocationException.class, () -> shippingCatalog.getShippingLocation(A_INVALID_SHIPPING_LOCATION_ID));
    }

    @Test
    public void givenInvalidLocationId_whenGetPickupLocation_shouldThrowInvalidLocationException() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().build();

        assertThrows(InvalidLocationException.class, () -> shippingCatalog.getPickupLocation(A_INVALID_SHIPPING_LOCATION_ID));
    }

    @Test
    public void whenGetShippingLocations_shouldReturnShippingLocations() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().addShippingLocation(A_SHIPPING_LOCATION).build();

        List<ShippingLocation> shippingLocations = shippingCatalog.getShippingLocations();

        assertEquals(shippingLocations, List.of(A_SHIPPING_LOCATION));
    }
}
