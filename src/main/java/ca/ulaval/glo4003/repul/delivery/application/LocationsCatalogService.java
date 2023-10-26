package ca.ulaval.glo4003.repul.delivery.application;

import ca.ulaval.glo4003.repul.delivery.application.exception.DeliverySystemNotFoundException;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;

public class LocationsCatalogService {
    private final DeliverySystemRepository deliverySystemRepository;

    public LocationsCatalogService(DeliverySystemRepository deliverySystemRepository) {
        this.deliverySystemRepository = deliverySystemRepository;
    }

    public DeliveryLocationsPayload getDeliveryLocations() {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        return new DeliveryLocationsPayload(deliverySystem.getDeliveryLocations().stream().map(
            deliveryLocation -> new DeliveryLocationPayload(deliveryLocation.getLocationId(), deliveryLocation.getName(), deliveryLocation.getTotalCapacity(),
                deliveryLocation.getRemainingCapacity())).toList());
    }
}