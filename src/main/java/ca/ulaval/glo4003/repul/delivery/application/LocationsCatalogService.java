package ca.ulaval.glo4003.repul.delivery.application;

import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;

public class LocationsCatalogService {
    private final DeliverySystemPersister deliverySystemPersister;

    public LocationsCatalogService(DeliverySystemPersister deliverySystemPersister) {
        this.deliverySystemPersister = deliverySystemPersister;
    }

    public DeliveryLocationsPayload getDeliveryLocations() {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        return new DeliveryLocationsPayload(deliverySystem.getDeliveryLocations().stream().map(
            deliveryLocation -> new DeliveryLocationPayload(deliveryLocation.getLocationId(), deliveryLocation.getName(), deliveryLocation.getTotalCapacity(),
                deliveryLocation.getRemainingCapacity())).toList());
    }
}
