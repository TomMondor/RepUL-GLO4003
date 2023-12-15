package ca.ulaval.glo4003.repul.delivery.application.payload;

import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocation;

public record DeliveryLocationPayload(
    String deliveryLocationId,
    String name,
    int totalCapacity,
    int remainingCapacity
) {
    public static DeliveryLocationPayload from(DeliveryLocation location) {
        return new DeliveryLocationPayload(location.getLocationId().toString(), location.getName(),
            location.getTotalCapacity(), location.getRemainingCapacity());
    }
}
