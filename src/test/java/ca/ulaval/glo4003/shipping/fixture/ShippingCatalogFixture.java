package ca.ulaval.glo4003.shipping.fixture;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.shipping.domain.catalog.ShippingCatalog;
import ca.ulaval.glo4003.shipping.domain.commons.PickupLocation;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

public class ShippingCatalogFixture {
    private final Map<LocationId, ShippingLocation> shippingLocations;
    private final Map<LocationId, PickupLocation> pickupLocations;

    public ShippingCatalogFixture() {
        this.shippingLocations = new HashMap<>();
        this.pickupLocations = new HashMap<>();
    }

    public ShippingCatalogFixture addShippingLocation(ShippingLocation shippingLocation) {
        shippingLocations.put(shippingLocation.getLocationId(), shippingLocation);
        return this;
    }

    public ShippingCatalogFixture addPickupLocation(PickupLocation pickupLocation) {
        pickupLocations.put(pickupLocation.getLocationId(), pickupLocation);
        return this;
    }

    public ShippingCatalog build() {
        return new ShippingCatalog(shippingLocations.values().stream().toList(), pickupLocations.values().stream().toList());
    }
}
