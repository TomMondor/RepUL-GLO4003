package ca.ulaval.glo4003.repul.delivery.application;

import java.util.List;

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

        List<DeliveryLocationPayload> deliveryLocations = deliverySystem.getDeliveryLocations().stream()
            .map(DeliveryLocationPayload::from).toList();

        return new DeliveryLocationsPayload(deliveryLocations);
    }
}
