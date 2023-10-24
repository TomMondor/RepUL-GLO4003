package ca.ulaval.glo4003.repul.small.shipping.domain.catalog;

import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.repul.fixture.shipping.ShippingCatalogFixture;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.ShippingCatalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShippingCatalogTest {
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
    public void givenValidLocationId_whenGetPickupLocation_shouldReturnRightPickupLocation() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).build();

        KitchenLocation kitchenLocation = shippingCatalog.getKitchenLocation(A_KITCHEN_LOCATION_ID);

        assertEquals(kitchenLocation, A_KITCHEN_LOCATION);
    }

    @Test
    public void givenValidLocationId_whenGetShippingLocation_shouldReturnRightShippingLocation() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().addDeliveryLocation(A_DELIVERY_LOCATION).build();

        DeliveryLocation deliveryLocation = shippingCatalog.getDeliveryLocation(A_DELIVERY_LOCATION_ID);

        assertEquals(deliveryLocation, A_DELIVERY_LOCATION);
    }

    @Test
    public void givenInvalidLocationId_whenGetShippingLocation_shouldThrowInvalidLocationException() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().build();

        assertThrows(InvalidLocationException.class, () -> shippingCatalog.getDeliveryLocation(A_INVALID_DELIVERY_LOCATION_ID));
    }

    @Test
    public void givenInvalidLocationId_whenGetPickupLocation_shouldThrowInvalidLocationException() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().build();

        assertThrows(InvalidLocationException.class, () -> shippingCatalog.getKitchenLocation(A_INVALID_KITCHEN_LOCATION_ID));
    }

    @Test
    public void whenGetShippingLocations_shouldReturnShippingLocations() {
        ShippingCatalog shippingCatalog = new ShippingCatalogFixture().addDeliveryLocation(A_DELIVERY_LOCATION).build();

        List<DeliveryLocation> deliveryLocations = shippingCatalog.getDeliveryLocations();

        assertEquals(deliveryLocations, List.of(A_DELIVERY_LOCATION));
    }
}
