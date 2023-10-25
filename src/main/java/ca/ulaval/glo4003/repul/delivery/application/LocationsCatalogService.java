package ca.ulaval.glo4003.repul.delivery.application;

import ca.ulaval.glo4003.repul.delivery.application.exception.DeliverySystemNotFoundException;
import ca.ulaval.glo4003.repul.delivery.application.payload.LocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;

public class LocationsCatalogService {
    private final DeliverySystemRepository deliverySystemRepository;

    public LocationsCatalogService(DeliverySystemRepository deliverySystemRepository) {
        this.deliverySystemRepository = deliverySystemRepository;
    }

    public LocationsPayload getDeliveryLocations() {
        DeliverySystem deliverySystem = deliverySystemRepository.get().orElseThrow(DeliverySystemNotFoundException::new);

        return new LocationsPayload(deliverySystem.getDeliveryLocations().stream().map(
            deliveryLocation -> new LocationPayload(deliveryLocation.getLocationId(), deliveryLocation.getName(), deliveryLocation.getTotalCapacity(),
                deliveryLocation.getRemainingCapacity())).toList());
    }
}
