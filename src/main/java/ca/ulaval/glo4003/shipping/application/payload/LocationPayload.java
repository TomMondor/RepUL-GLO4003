package ca.ulaval.glo4003.shipping.application.payload;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

public record LocationPayload(LocationId locationId, String name, int totalCapacity, int remainingCapacity) {
    public static LocationPayload from(ShippingLocation location) {
        return new LocationPayload(location.getLocationId(), location.getName(),
            location.getTotalCapacity(), location.getRemainingCapacity());
    }
}
