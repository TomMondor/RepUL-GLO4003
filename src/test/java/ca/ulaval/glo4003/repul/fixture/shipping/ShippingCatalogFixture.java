package ca.ulaval.glo4003.repul.fixture.shipping;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.ShippingCatalog;

public class ShippingCatalogFixture {
    private final Map<DeliveryLocationId, DeliveryLocation> deliveryLocations;
    private final Map<KitchenLocationId, KitchenLocation> kitchenLocations;

    public ShippingCatalogFixture() {
        this.deliveryLocations = new HashMap<>();
        this.kitchenLocations = new HashMap<>();
    }

    public ShippingCatalogFixture addDeliveryLocation(DeliveryLocation deliveryLocation) {
        deliveryLocations.put(deliveryLocation.getLocationId(), deliveryLocation);
        return this;
    }

    public ShippingCatalogFixture addKitchenLocation(KitchenLocation kitchenLocation) {
        kitchenLocations.put(kitchenLocation.getLocationId(), kitchenLocation);
        return this;
    }

    public ShippingCatalog build() {
        return new ShippingCatalog(deliveryLocations.values().stream().toList(), kitchenLocations.values().stream().toList());
    }
}
