package ca.ulaval.glo4003.repul.shipping.application.payload;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;

public record LocationPayload(DeliveryLocationId deliveryLocationId, String name, int totalCapacity, int remainingCapacity) {
    public static LocationPayload from(DeliveryLocation location) {
        return new LocationPayload(location.getLocationId(), location.getName(),
            location.getTotalCapacity(), location.getRemainingCapacity());
    }
}
