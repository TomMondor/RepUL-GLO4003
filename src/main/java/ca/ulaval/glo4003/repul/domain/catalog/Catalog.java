package ca.ulaval.glo4003.repul.domain.catalog;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.domain.Exception.InvalidLocationException;

public class Catalog {
    private final Map<LocationId, PickupLocation> pickupLocations;

    public Catalog(List<PickupLocation> pickupLocations) {
        this.pickupLocations = pickupLocations.stream().collect(Collectors.toMap(PickupLocation::getLocationId, Function.identity()));
    }

    public PickupLocation getPickupLocation(LocationId locationId) {
        if (!pickupLocations.containsKey(locationId)) {
            throw new InvalidLocationException();
        }
        return pickupLocations.get(locationId);
    }
}
