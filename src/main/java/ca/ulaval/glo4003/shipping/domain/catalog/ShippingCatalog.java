package ca.ulaval.glo4003.shipping.domain.catalog;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.commons.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.shipping.domain.commons.PickupLocation;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

public class ShippingCatalog {
    private final Map<LocationId, ShippingLocation> shippingLocations;
    private final Map<LocationId, PickupLocation> pickupLocations;

    public ShippingCatalog(List<ShippingLocation> shippingLocations, List<PickupLocation> pickupLocations) {
        this.shippingLocations = shippingLocations.stream().collect(Collectors.toMap(ShippingLocation::getLocationId, Function.identity()));
        this.pickupLocations = pickupLocations.stream().collect(Collectors.toMap(PickupLocation::getLocationId, Function.identity()));
    }

    public ShippingLocation getShippingLocation(LocationId locationId) {
        if (shippingLocations.containsKey(locationId)) {
            return shippingLocations.get(locationId);
        }
        throw new InvalidLocationException();
    }

    public PickupLocation getPickupLocation(LocationId locationId) {
        if (pickupLocations.containsKey(locationId)) {
            return pickupLocations.get(locationId);
        }
        throw new InvalidLocationException();
    }

    public List<ShippingLocation> getShippingLocations() {
        return shippingLocations.values().stream().toList();
    }
}
