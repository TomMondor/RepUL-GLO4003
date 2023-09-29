package ca.ulaval.glo4003.repul.application.catalog.payload;

import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

public record LocationPayload(LocationId locationId, String name, int totalCapacity, int remainingCapacity) {
    public static LocationPayload from(PickupLocation location) {
        return new LocationPayload(location.getLocationId(), location.getName(), location.getTotalCapacity(), location.getRemainingCapacity());
    }
}
